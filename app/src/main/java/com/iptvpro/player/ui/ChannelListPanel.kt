package com.iptvpro.player.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun ChannelListPanel(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val channels by viewModel.channels.collectAsState()
    val currentChannel by viewModel.currentChannel.collectAsState()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(channels) { channel ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectChannel(channel) }
                    .padding(8.dp)
            ) {
                Text(
                    text = channel.name,
                    color = if (channel == currentChannel) Color(0xFFE50914) else Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}
