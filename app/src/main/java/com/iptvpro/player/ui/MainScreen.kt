package com.iptvpro.player.ui
import android.view.KeyEvent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iptvpro.player.theme.DeepBlack
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun MainScreen(vm: PlayerViewModel = viewModel()) {
    val loading by vm.loading.collectAsState()
    val showList by vm.showList.collectAsState()
    val showBot by vm.showBottom.collectAsState()
    val showEpg by vm.showEpg.collectAsState()
    val showMenu by vm.showMenu.collectAsState()
    val err by vm.err.collectAsState()

    Box(Modifier.fillMaxSize().background(DeepBlack).onKeyEvent { e ->
        if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
        when (e.nativeKeyEvent.keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> { vm.chUp(); true }
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> { vm.chDown(); true }
            KeyEvent.KEYCODE_DPAD_LEFT -> { vm.toggleList(); true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { vm.toggleMenu(); true }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { vm.toggleBottom(); true }
            KeyEvent.KEYCODE_BACK -> { vm.hideAll(); true }
            KeyEvent.KEYCODE_MENU -> { vm.toggleMenu(); true }
            KeyEvent.KEYCODE_INFO, KeyEvent.KEYCODE_TV_DATA_SERVICE, KeyEvent.KEYCODE_GUIDE -> { vm.toggleEpg(); true }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { vm.pm.exoPlayer.playWhenReady = !vm.pm.exoPlayer.playWhenReady; true }
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> { vm.select(e.nativeKeyEvent.keyCode - KeyEvent.KEYCODE_0); true }
            else -> false
        }
    }) {
        PlayerScreen(vm, Modifier.fillMaxSize())
        if (loading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 4.dp, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(14.dp)); Text("Kanallar yükleniyor...", color = Color.White)
            }
        }
        AnimatedVisibility(showList, enter = slideInHorizontally { -it } + fadeIn(), exit = slideOutHorizontally { -it } + fadeOut(), modifier = Modifier.align(Alignment.CenterStart)) { ChannelListPanel(vm) }
        AnimatedVisibility(showBot, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut(), modifier = Modifier.align(Alignment.BottomCenter)) { BottomInfoPanel(vm) }
        AnimatedVisibility(showEpg, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut(), modifier = Modifier.align(Alignment.BottomCenter)) { EpgPanel(vm) }
        AnimatedVisibility(showMenu, enter = slideInHorizontally { it } + fadeIn(), exit = slideOutHorizontally { it } + fadeOut(), modifier = Modifier.align(Alignment.CenterEnd)) { QuickMenu(vm) }
        err?.let { m -> Snackbar(modifier = Modifier.align(Alignment.TopCenter).padding(16.dp), action = { TextButton(onClick = { vm.clearErr() }) { Text("Kapat", color = Color.White) } }, containerColor = MaterialTheme.colorScheme.error) { Text(m, color = Color.White) } }
    }
}
