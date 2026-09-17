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
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                ChannelListPanel(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            }
            BottomInfoPanel(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
