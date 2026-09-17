package com.iptvpro.player.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BottomInfoPanel(vm: PlayerViewModel) {
    val ch by vm.current.collectAsState()
    val ci by vm.idx.collectAsState()
    val now by vm.now.collectAsState()
    val next by vm.next.collectAsState()
    val st by vm.state.collectAsState()
    val tf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val clock = remember { mutableStateOf("") }
    LaunchedEffect(Unit) { while(true) { clock.value = tf.format(Date()); kotlinx.coroutines.delay(1000) } }

    Surface(modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp), shape = RoundedCornerShape(20.dp), color = Color.Transparent, shadowElevation = 24.dp) {
        Box(modifier = Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(DeepBlack.copy(.85f), DeepBlack.copy(.95f))), RoundedCornerShape(20.dp))
            .border(1.dp, Brush.horizontalGradient(listOf(CrimsonRed.copy(.5f), DarkRed.copy(.3f))), RoundedCornerShape(20.dp))
            .padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = CrimsonRed, modifier = Modifier.size(52.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("${ci+1}", color = BrightWhite, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }
                }
                Spacer(Modifier.width(12.dp))
                ch?.let { if (it.logo.isNotEmpty()) { AsyncImage(it.logo, it.name, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(CardBlack), contentScale = ContentScale.Fit); Spacer(Modifier.width(12.dp)) } }
                Column(Modifier.weight(1f)) {
                    Text(ch?.name ?: "Kanal Seçiniz", color = BrightWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(EpgNow)); Spacer(Modifier.width(6.dp))
                        Text("Şimdi: ${now?.title ?: ch?.group ?: ""}", color = EpgNow, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    now?.let { Spacer(Modifier.height(3.dp)); LinearProgressIndicator(progress = { it.progress }, modifier = Modifier.fillMaxWidth(.7f).height(3.dp).clip(RoundedCornerShape(2.dp)), color = CrimsonRed, trackColor = DarkGray) }
                    next?.let { Spacer(Modifier.height(3.dp)); Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(EpgNext)); Spacer(Modifier.width(6.dp))
                        Text("Sıradaki: ${it.title}", color = EpgNext, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    } }
                }
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    val sc = when(st) { PState.PLAYING -> EpgNow; PState.LOADING -> EpgNext; PState.ERROR -> AccentRed; else -> SubtleGray }
                    val si = when(st) { PState.PLAYING -> Icons.Default.PlayArrow; PState.LOADING -> Icons.Default.HourglassTop; PState.PAUSED -> Icons.Default.Pause; PState.ERROR -> Icons.Default.Error; else -> Icons.Default.Stop }
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(si, null, tint = sc, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp))
                        Text(when(st) { PState.PLAYING -> "CANLI"; PState.LOADING -> "Yükleniyor"; PState.PAUSED -> "Duraklatıldı"; PState.ERROR -> "Hata"; else -> "" }, color = sc, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = CardBlack, border = BorderStroke(1.dp, DarkGray)) {
                        Text(clock.value, Modifier.padding(12.dp, 4.dp), color = BrightWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    ch?.let { Surface(shape = RoundedCornerShape(4.dp), color = if (it.isAddon) AccentRed.copy(.2f) else DarkGray.copy(.5f)) {
                        Text(if (it.isAddon) "ADDON" else "M3U", Modifier.padding(8.dp, 2.dp), color = if (it.isAddon) AccentRed else SubtleGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    } }
                }
            }
        }
    }
}
