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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun QuickMenu(vm: PlayerViewModel) {
    var quality by remember { mutableStateOf("Auto") }
    Surface(modifier = Modifier.width(280.dp).fillMaxHeight().padding(8.dp),
        shape = RoundedCornerShape(16.dp), color = DeepBlack.copy(.97f),
        border = BorderStroke(1.dp, CrimsonRed.copy(.3f)), shadowElevation = 24.dp) {
        Column {
            Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(CrimsonRed, DarkRed))).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Tune, null, tint = BrightWhite, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(8.dp)); Text("Hızlı Menü", color = BrightWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
            }
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionTitle("Kalite", Icons.Default.HighQuality)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("SD","HD","Max").forEach { q ->
                        val s = quality == q
                        Surface(Modifier.weight(1f).clickable { quality = q; vm.pm.setQuality(q) },
                            shape = RoundedCornerShape(10.dp), color = if (s) CrimsonRed else CardBlack,
                            border = if (!s) BorderStroke(1.dp, DarkGray) else null) {
                            Text(q, Modifier.padding(vertical = 10.dp).fillMaxWidth(), color = BrightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                MItem(Icons.Default.Audiotrack, "Ses Parçası", "Varsayılan") {}
                MItem(Icons.Default.Subtitles, "Altyazı", "Kapalı") {}
                Divider(color = DarkGray, thickness = .5.dp, modifier = Modifier.padding(vertical = 4.dp))
                MItem(Icons.Default.EventNote, "Program Rehberi", "EPG") { vm.toggleEpg() }
                MItem(Icons.Default.FormatListBulleted, "Kanal Listesi", "Kanallar") { vm.toggleList() }
                Divider(color = DarkGray, thickness = .5.dp, modifier = Modifier.padding(vertical = 4.dp))
                MItem(Icons.Default.Refresh, "Yenile", "Tüm kaynaklar") { vm.refresh() }
                Spacer(Modifier.height(12.dp))
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = CardBlack, border = BorderStroke(1.dp, DarkGray.copy(.5f))) {
                    Column(Modifier.padding(12.dp)) {
                        Text("🎮 Kumanda", color = CrimsonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        listOf("▲▼" to "Kanal", "◄" to "Liste", "►" to "Menü", "OK" to "Bilgi", "INFO" to "EPG").forEach { (k, d) ->
                            Row(Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(4.dp), color = DarkGray, modifier = Modifier.width(42.dp)) { Text(k, Modifier.padding(4.dp, 2.dp), color = BrightWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
                                Spacer(Modifier.width(8.dp)); Text(d, color = SubtleGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable private fun SectionTitle(t: String, i: ImageVector) { Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Icon(i, null, tint = CrimsonRed, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text(t, color = CrimsonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
@Composable private fun MItem(icon: ImageVector, title: String, sub: String, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp), color = CardBlack.copy(.5f)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = CrimsonRed, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, color = BrightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold); Text(sub, color = SubtleGray, fontSize = 11.sp) }
            Icon(Icons.Default.ChevronRight, null, tint = SubtleGray, modifier = Modifier.size(16.dp))
        }
    }
}
