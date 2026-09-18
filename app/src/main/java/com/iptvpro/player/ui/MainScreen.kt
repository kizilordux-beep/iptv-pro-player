package com.iptvpro.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iptvpro.player.viewmodel.PlayerViewModel

val TvvooRed = Color(0xFFE50914)
val TvvooBg = Color(0xFF0D0D0D)
val TvvooCard = Color(0xFF161616)
val TvvooBorder = Color(0xFF2A2A2A)

@Composable
fun MainScreen(
    viewModel: PlayerViewModel = viewModel()
) {
    val currentChannel by viewModel.currentChannel.collectAsState()
    val channels by viewModel.channels.collectAsState()
    var workersUrlInput by remember { mutableStateOf(viewModel.workersUrl.value) }
    var manifestUrlInput by remember { mutableStateOf(viewModel.manifestUrl.value) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TvvooBg)
            .padding(12.dp)
    ) {
        // Üst Başlık
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Text("▶ ", color = TvvooRed, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("TVVOO", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("GÜÇLÜ IPTV OYNATICI", color = TvvooRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.weight(1f)) {
            // Sol Yan Panel (Özellikler & Çift Kaynak Yönetimi)
            Column(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .padding(end = 8.dp)
            ) {
                // Kaynak Yönetimi Kutuları (Workers & Manifest)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TvvooCard, RoundedCornerShape(8.dp))
                        .border(1.dp, TvvooBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text("🌐 Workers URL", color = Color.White, fontSize = 11.sp)
                    OutlinedTextField(
                        value = workersUrlInput,
                        onValueChange = { workersUrlInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.LightGray, fontSize = 10.sp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🔗 Manifest URL", color = Color.White, fontSize = 11.sp)
                    OutlinedTextField(
                        value = manifestUrlInput,
                        onValueChange = { manifestUrlInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.LightGray, fontSize = 10.sp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.workersUrl.value = workersUrlInput
                            viewModel.manifestUrl.value = manifestUrlInput
                            viewModel.loadDualSources()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TvvooRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kaynakları Yenile", color = Color.White, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Kanal Listesi
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TvvooCard, RoundedCornerShape(8.dp))
                        .border(1.dp, TvvooBorder, RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    items(channels) { ch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectChannel(ch) }
                                .padding(8.dp)
                        ) {
                            Text(ch.name, color = if (ch == currentChannel) TvvooRed else Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Sağ Video Alanı (ExoPlayer)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .border(1.dp, TvvooBorder, RoundedCornerShape(8.dp))
            ) {
                currentChannel?.let { ch ->
                    VideoPlayer(url = ch.url, modifier = Modifier.fillMaxSize())
                } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Kanal Yükleniyor...", color = Color.Gray)
                }
            }
        }
    }
}
