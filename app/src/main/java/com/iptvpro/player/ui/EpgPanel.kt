package com.iptvpro.player.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iptvpro.player.model.EpgProgram
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EpgPanel(vm: PlayerViewModel) {
    val ch by vm.current.collectAsState()
    val epg by vm.epgList.collectAsState()
    val now by vm.now.collectAsState()
    val next by vm.next.collectAsState()
    val tf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Surface(modifier = Modifier.fillMaxWidth().fillMaxHeight(.55f).padding(12.dp, 8.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = DeepBlack.copy(.97f), border = BorderStroke(1.dp, CrimsonRed.copy(.4f)), shadowElevation = 24.dp) {
        Column {
            Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(DarkRed, CrimsonRed, DarkRed))).padding(20.dp, 14.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.EventNote, null, tint = BrightWhite, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(8.dp)); Text("Program Rehberi", color = BrightWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
                    Text(ch?.name ?: "", color = BrightWhite.copy(.7f), fontSize = 13.sp)
                }
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EpgCard("ŞİMDİ", EpgNow, now?.title, now, tf)
                EpgCard("SIRADAKİ", EpgNext, next?.title, next, tf)
            }
            if (epg.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.EventBusy, null, tint = SubtleGray, modifier = Modifier.size(44.dp)); Spacer(Modifier.height(8.dp)); Text("EPG mevcut değil", color = SubtleGray, fontSize = 13.sp) }
            } else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(epg) { p ->
                    val live = p.isLive; val past = p.endTime < System.currentTimeMillis()
                    Surface(shape = RoundedCornerShape(10.dp), color = if (live) CrimsonRed.copy(.15f) else CardBlack.copy(.3f),
                        border = if (live) BorderStroke(1.dp, CrimsonRed.copy(.5f)) else null) {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.width(56.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tf.format(Date(p.startTime)), color = if (live) CrimsonRed else if (past) SubtleGray.copy(.5f) else DimWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(tf.format(Date(p.endTime)), color = SubtleGray.copy(.6f), fontSize = 10.sp)
                            }
                            Box(Modifier.width(3.dp).height(32.dp).clip(RoundedCornerShape(2.dp)).background(if (live) CrimsonRed else DarkGray))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) { Text(p.title, color = if (past) SubtleGray.copy(.5f) else BrightWhite, fontSize = 13.sp, fontWeight = if (live) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            if (live) Surface(shape = RoundedCornerShape(6.dp), color = CrimsonRed) { Text("CANLI", Modifier.padding(8.dp, 3.dp), color = BrightWhite, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpgCard(label: String, color: androidx.compose.ui.graphics.Color, title: String?, prog: EpgProgram?, tf: SimpleDateFormat) {
    Surface(Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = color.copy(.1f), border = BorderStroke(1.dp, color.copy(.3f))) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(10.dp).clip(CircleShape).background(color)); Spacer(Modifier.width(6.dp)); Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(5.dp))
            Text(title ?: "Bilgi yok", color = BrightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            prog?.let { Spacer(Modifier.height(3.dp)); Text("${tf.format(Date(it.startTime))} - ${tf.format(Date(it.endTime))}", color = SubtleGray, fontSize = 10.sp)
                if (it.isLive) { Spacer(Modifier.height(4.dp)); LinearProgressIndicator(progress = { it.progress }, Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)), color = color, trackColor = DarkGray) }
            }
        }
    }
}
