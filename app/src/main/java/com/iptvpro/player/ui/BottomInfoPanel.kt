package com.iptvpro.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun BottomInfoPanel(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val currentChannel by viewModel.currentChannel.collectAsState()

    Row(
        modifier = modifier
            .background(Color(0xFF161616))
            .padding(8.dp)
    ) {
        Text(
            text = "Oynatılıyor: ${currentChannel?.name ?: "Kanal Seçilmedi"}",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
