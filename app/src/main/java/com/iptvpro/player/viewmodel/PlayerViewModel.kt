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

enum class PState { IDLE, BUFFERING, PLAYING, ERROR }

class PlayerViewModel : ViewModel() {
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _current = MutableStateFlow<Channel?>(null)
    val currentChannel: StateFlow<Channel?> = _current.asStateFlow()

    private val _state = MutableStateFlow(PState.IDLE)
    val state: StateFlow<PState> = _state.asStateFlow()

    var workersUrl = MutableStateFlow("https://room.coinmybook.workers.dev/playlist.m3u")
    var manifestUrl = MutableStateFlow("https://tvvoo.hayd.uk/cfg-tr/manifest.json")

    init {
        loadDualSources()
    }

    fun loadDualSources() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = PState.BUFFERING
            val mergedList = mutableListOf<Channel>()

            // 1. Workers M3U Yükle
            try {
                val m3uText = fetchUrlContent(workersUrl.value)
                mergedList.addAll(parseM3u(m3uText))
            } catch (e: Exception) { e.printStackTrace() }

            // 2. Manifest JSON Yükle
            try {
                val jsonText = fetchUrlContent(manifestUrl.value)
                mergedList.addAll(parseManifestJson(jsonText, manifestUrl.value))
            } catch (e: Exception) { e.printStackTrace() }

            if (mergedList.isNotEmpty()) {
                _channels.value = mergedList
                _current.value = mergedList[0]
                _state.value = PState.PLAYING
            } else {
                _state.value = PState.ERROR
            }
        }
    }

    private fun fetchUrlContent(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun parseM3u(m3uText: String): List<Channel> {
        val list = mutableListOf<Channel>()
        var currentName = ""
        var currentGroup = "Workers M3U"

        for (line in m3uText.lines()) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                val nameMatch = Regex(".*,(.*)").find(trimmed)
                currentName = nameMatch?.groupValues?.get(1)?.trim() ?: "Kanal"
            } else if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                list.add(Channel(id = list.size.toString(), name = currentName, url = trimmed, group = currentGroup))
                currentName = ""
            }
        }
        return list
    }

    private fun parseManifestJson(jsonStr: String, baseUrl: String): List<Channel> {
        val list = mutableListOf<Channel>()
        try {
            val root = JSONObject(jsonStr)
            val addonName = root.optString("name", "Manifest Addon")
            val catalogs = root.optJSONArray("catalogs") ?: JSONArray()
            val rootUrl = baseUrl.substringBeforeLast("/")

            for (i in 0 until catalogs.length()) {
                val cat = catalogs.getJSONObject(i)
                val type = cat.optString("type", "tv")
                val id = cat.optString("id", "channels")
                val catContent = fetchUrlContent("$rootUrl/catalog/$type/$id.json")
                val metas = JSONObject(catContent).optJSONArray("metas") ?: JSONArray()

                for (j in 0 until metas.length()) {
                    val meta = metas.getJSONObject(j)
                    val title = meta.optString("name", "Kanal")
                    val channelId = meta.optString("id", "")
                    list.add(Channel(id = channelId, name = title, url = "$rootUrl/stream/$type/$channelId.json", group = addonName, isAddon = true))
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    fun selectChannel(channel: Channel) {
        _current.value = channel
    }
}
