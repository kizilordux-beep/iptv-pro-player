package com.iptvpro.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
                val text = URL(playlistUrl).readText()
                val parsedChannels = parseM3u(text)
                _channels.value = parsedChannels
                _filtered.value = parsedChannels
                if (parsedChannels.isNotEmpty()) {
                    _current.value = parsedChannels[0]
                    _idx.value = 0
                }
                _state.value = PState.PLAYING
            } catch (e: Exception) {
                _state.value = PState.ERROR
            }
        }
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
                        name = currentName,
                        url = trimmed,
                        logo = currentLogo,
                        group = currentGroup
                    )
                )
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
