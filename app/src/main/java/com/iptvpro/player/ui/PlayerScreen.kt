package com.iptvpro.player.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun PlayerScreen(vm: PlayerViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Color.Black).clickable { vm.showBottomTemp() }) {
        AndroidView(
            factory = { c -> PlayerView(c).apply { player = vm.pm.exoPlayer; useController = false; keepScreenOn = true } },
            modifier = Modifier.fillMaxSize(),
            update = { it.player = vm.pm.exoPlayer }
        )
    }
}
