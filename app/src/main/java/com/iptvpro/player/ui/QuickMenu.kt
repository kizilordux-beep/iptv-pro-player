package com.iptvpro.player.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun QuickMenu(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { viewModel.toggleEpg() }) {
            Text("EPG Göster/Gizle")
        }
    }
}
