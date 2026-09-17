package com.iptvpro.player.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    fun select(channel: Channel) {
        _current.value = channel
    }

    fun playChannel(channel: Channel) {
        select(channel)
    }

    fun setGroup(groupName: String) {
        _group.value = groupName
    }

    fun toggleEpg() {
        _isEpgOpen.value = !_isEpgOpen.value
    }
}
