package com.iptvpro.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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
            // Sol Yan Panel
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .padding(end = 8.dp)
            ) {
                // Kaynak Yönetimi
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TvvooCard, RoundedCornerShape(8.dp))
                        .border(1.dp, TvvooBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text("🌐 Workers URL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = workersUrlInput,
                            onValueChange = { workersUrlInput = it },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.LightGray, fontSize = 10.sp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = {
                                viewModel.workersUrl.value = workersUrlInput
                                viewModel.loadWorkersSource()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TvvooRed),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Yükle", color = Color.White, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("🔗 Manifest URL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = manifestUrlInput,
                            onValueChange = { manifestUrlInput = it },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.LightGray, fontSize = 10.sp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = {
                                viewModel.manifestUrl.value = manifestUrlInput
                                viewModel.loadManifestSource()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TvvooRed),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Yükle", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Logolu Grid Kanal Listesi (2 Sütunlu)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TvvooCard, RoundedCornerShape(8.dp))
                        .border(1.dp, TvvooBorder, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(channels) { ch ->
                        val isSelected = ch == currentChannel
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) TvvooRed.copy(alpha = 0.2f) else Color(0xFF222222),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) TvvooRed else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectChannel(ch) }
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (ch.logo.isNotEmpty()) {
                                AsyncImage(
                                    model = ch.logo,
                                    contentDescription = ch.name,
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth()
                                        .background(Color.DarkGray, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("NO LOGO", color = Color.Gray, fontSize = 9.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = ch.name,
                                color = if (isSelected) TvvooRed else Color.White,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Sağ Video Ekranı
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
