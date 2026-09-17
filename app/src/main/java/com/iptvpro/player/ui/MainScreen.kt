package com.iptvpro.player.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iptvpro.player.theme.NetflixRed
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel()
) {
    var urlText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val currentChannel by viewModel.currentChannel.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ExoPlayer Video Alanı
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    currentChannel?.let { channel ->
                        if (channel.url.isNotEmpty()) {
                            VideoPlayer(
                                url = channel.url,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lütfen Oynatmak İçin Bir Kanal Seçin",
                            color = Color.White
                        )
                    }
                }

                // Kanal Listesi Paneli
                ChannelListPanel(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            // Kaynak Yönetimi Butonu
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NetflixRed)
                ) {
                    Text("Kaynak Ekle (M3U / JSON)", color = Color.White)
                }
            }

            // M3U / Manifest Dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = { Text("Kaynak Yöneticisi", color = Color.White) },
                    text = {
                        Column {
                            Text("M3U veya Manifest JSON URL girin:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = urlText,
                                onValueChange = { urlText = it },
                                label = { Text("Playlist / Manifest URL") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (urlText.isNotBlank()) {
                                    viewModel.loadPlaylistFromUrl(urlText)
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NetflixRed)
                        ) {
                            Text("Yükle", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("İptal", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}
