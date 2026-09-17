package com.iptvpro.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Channel(
    val id: String = "",
    val name: String = "",
    val url: String = "",
    val logo: String = "",
    val group: String = "",
    val isAddon: Boolean = false
)

data class EpgProgram(
    val title: String = "",
    val progress: Float = 0f
)

enum class PState {
    IDLE, BUFFERING, PLAYING, ERROR
}

class PlayerViewModel : ViewModel() {
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _filtered = MutableStateFlow<List<Channel>>(emptyList())
    val filtered: StateFlow<List<Channel>> = _filtered.asStateFlow()

    private val _current = MutableStateFlow<Channel?>(null)
    val current: StateFlow<Channel?> = _current.asStateFlow()

    private val _idx = MutableStateFlow(0)
    val idx: StateFlow<Int> = _idx.asStateFlow()

    private val _groups = MutableStateFlow<List<String>>(emptyList())
    val groups: StateFlow<List<String>> = _groups.asStateFlow()

    private val _group = MutableStateFlow("Tümü")
    val group: StateFlow<String> = _group.asStateFlow()

    private val _now = MutableStateFlow<EpgProgram?>(null)
    val now: StateFlow<EpgProgram?> = _now.asStateFlow()

    private val _next = MutableStateFlow<EpgProgram?>(null)
    val next: StateFlow<EpgProgram?> = _next.asStateFlow()

    private val _state = MutableStateFlow(PState.IDLE)
    val state: StateFlow<PState> = _state.asStateFlow()

    private val _isEpgOpen = MutableStateFlow(false)
    val isEpgOpen: StateFlow<Boolean> = _isEpgOpen.asStateFlow()

    val currentChannel: StateFlow<Channel?> = _current.asStateFlow()

    fun loadPlaylistFromUrl(playlistUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.value = PState.BUFFERING
                val content = fetchUrlContent(playlistUrl)

                val parsedChannels = if (playlistUrl.endsWith("manifest.json") || content.trim().startsWith("{")) {
                    parseManifestJson(content, playlistUrl)
                } else {
                    parseM3u(content)
                }

                if (parsedChannels.isNotEmpty()) {
                    _channels.value = parsedChannels
                    _filtered.value = parsedChannels
                    _current.value = parsedChannels[0]
                    _idx.value = 0
                    _state.value = PState.PLAYING
                } else {
                    _state.value = PState.ERROR
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = PState.ERROR
            }
        }
    }

    private fun fetchUrlContent(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
        connection.connectTimeout = 12000
        connection.readTimeout = 12000
        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun parseManifestJson(jsonStr: String, baseUrl: String): List<Channel> {
        val list = mutableListOf<Channel>()
        try {
            val root = JSONObject(jsonStr)
            val addonName = root.optString("name", "Addon")
            
            // Catalogue veya Stream endpointlerini ayıkla
            val catalogs = root.optJSONArray("catalogs") ?: JSONArray()
            val rootUrl = baseUrl.substringBeforeLast("/")

            if (catalogs.length() > 0) {
                for (i in 0 until catalogs.length()) {
                    val cat = catalogs.getJSONObject(i)
                    val type = cat.optString("type", "tv")
                    val id = cat.optString("id", "channels")
                    
                    val catalogUrl = "$rootUrl/catalog/$type/$id.json"
                    try {
                        val catContent = fetchUrlContent(catalogUrl)
                        val catObj = JSONObject(catContent)
                        val metas = catObj.optJSONArray("metas") ?: JSONArray()
                        
                        for (j in 0 until metas.length()) {
                            val meta = metas.getJSONObject(j)
                            val title = meta.optString("name", "Kanal ${list.size + 1}")
                            val logo = meta.optString("poster", meta.optString("logo", ""))
                            val channelId = meta.optString("id", "")
                            
                            // Stream URL veya Proxy yapısı
                            val streamUrl = "$rootUrl/stream/$type/$channelId.json"
                            list.add(
                                Channel(
                                    id = channelId,
                                    name = title,
                                    url = streamUrl,
                                    logo = logo,
                                    group = addonName,
                                    isAddon = true
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                // Doğrudan tanım varsa Addon adıyla ekle
                list.add(
                    Channel(
                        id = root.optString("id", "1"),
                        name = addonName,
                        url = baseUrl,
                        group = "Addon"
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun parseM3u(m3uText: String): List<Channel> {
        val list = mutableListOf<Channel>()
        val lines = m3uText.lines()
        var currentName = ""
        var currentLogo = ""
        var currentGroup = ""

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                val nameMatch = Regex(".*,(.*)").find(trimmed)
                currentName = nameMatch?.groupValues?.get(1)?.trim() ?: "Kanal"

                val logoMatch = Regex("""tvg-logo="([^"]+)"""").find(trimmed)
                currentLogo = logoMatch?.groupValues?.get(1) ?: ""

                val groupMatch = Regex("""group-title="([^"]+)"""").find(trimmed)
                currentGroup = groupMatch?.groupValues?.get(1) ?: "Genel"
            } else if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                list.add(
                    Channel(
                        id = list.size.toString(),
                        name = if (currentName.isEmpty()) "Kanal ${list.size + 1}" else currentName,
                        url = trimmed,
                        logo = currentLogo,
                        group = currentGroup
                    )
                )
                currentName = ""
            }
        }
        return list
    }

    fun select(channel: Channel) {
        _current.value = channel
        _idx.value = _filtered.value.indexOf(channel).coerceAtLeast(0)
    }

    fun playChannel(channel: Channel) {
        select(channel)
    }

    fun setGroup(groupName: String) {
        _group.value = groupName
        _filtered.value = if (groupName == "Tümü") {
            _channels.value
        } else {
            _channels.value.filter { it.group == groupName }
        }
    }

    fun toggleEpg() {
        _isEpgOpen.value = !_isEpgOpen.value
    }
}
