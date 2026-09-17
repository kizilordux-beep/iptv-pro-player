package com.iptvpro.player.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel()
) {
    val channels by viewModel.channels.collectAsState()
    val currentChannel by viewModel.currentChannel.collectAsState()
    val isEpgOpen by viewModel.isEpgOpen.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Video Oynatıcı Paneli
            VideoPlayer(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )

            // Sol Taraf: Kanal Listesi Overlay
            Row(modifier = Modifier.fillMaxSize()) {
                ChannelList(
                    channels = channels,
                    currentChannel = currentChannel,
                    onChannelSelect = { channel ->
                        viewModel.playChannel(channel)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Sağ Taraf: EPG Paneli (Açıksa gösterilir)
                if (isEpgOpen) {
                    EpgPanel(
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
