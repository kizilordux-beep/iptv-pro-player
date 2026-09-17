package com.iptvpro.player.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iptvpro.player.viewmodel.PState
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun BottomInfoPanel(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val current by viewModel.current.collectAsState()
    val idx by viewModel.idx.collectAsState()
    val now by viewModel.now.collectAsState()
    val next by viewModel.next.collectAsState()
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${idx + 1}. ${current?.name ?: "Kanal Seçilmedi"}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Şimdi: ${now?.title ?: "Bilgi Yok"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (now != null) {
                    LinearProgressIndicator(
                        progress = { now?.progress ?: 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
                Text(
                    text = "Sonra: ${next?.title ?: "Bilgi Yok"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = when (state) {
                    PState.PLAYING -> "Oynatılıyor"
                    PState.BUFFERING -> "Yükleniyor..."
                    PState.ERROR -> "Hata"
                    PState.IDLE -> "Hazır"
                },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
