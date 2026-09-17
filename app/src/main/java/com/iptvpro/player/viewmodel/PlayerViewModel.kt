package com.iptvpro.player.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Channel(
    val id: String = "",
    val name: String = "",
    val url: String = ""
)

class PlayerViewModel : ViewModel() {
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _currentChannel = MutableStateFlow<Channel?>(null)
    val currentChannel: StateFlow<Channel?> = _currentChannel.asStateFlow()

    private val _isEpgOpen = MutableStateFlow(false)
    val isEpgOpen: StateFlow<Boolean> = _isEpgOpen.asStateFlow()

    fun playChannel(channel: Channel) {
        _currentChannel.value = channel
    }

    fun toggleEpg() {
        _isEpgOpen.value = !_isEpgOpen.value
    }
}
