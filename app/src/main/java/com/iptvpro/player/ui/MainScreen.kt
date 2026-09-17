package com.iptvpro.player.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel()
) {
    var urlText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

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

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Button(onClick = { showDialog = true }) {
                    Text("Kaynak Ekle (M3U)")
                }
            }

            BottomInfoPanel(
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("M3U Playlist URL Girin") },
                    text = {
                        OutlinedTextField(
                            value = urlText,
                            onValueChange = { urlText = it },
                            label = { Text("Playlist URL (http/https)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (urlText.isNotBlank()) {
                                    viewModel.loadPlaylistFromUrl(urlText)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Yükle")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("İptal")
                        }
                    }
                )
            }
        }
    }
}
