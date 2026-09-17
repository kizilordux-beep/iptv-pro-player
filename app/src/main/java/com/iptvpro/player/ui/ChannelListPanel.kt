package com.iptvpro.player.ui
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iptvpro.player.theme.*
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun ChannelListPanel(vm: PlayerViewModel) {
    val channels by vm.filtered.collectAsState()
    val ci by vm.idx.collectAsState()
    val groups by vm.groups.collectAsState()
    val sg by vm.group.collectAsState()
    val ls = rememberLazyListState()
    LaunchedEffect(ci) { if (channels.isNotEmpty()) ls.animateScrollToItem(ci.coerceIn(0, channels.size-1)) }

    Surface(
        modifier = Modifier.width(360.dp).fillMaxHeight().padding(8.dp),
        shape = RoundedCornerShape(16.dp), color = DeepBlack.copy(.95f),
        border = BorderStroke(1.dp, CrimsonRed.copy(.3f)), shadowElevation = 16.dp
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(DarkRed, CrimsonRed))).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LiveTv, null, tint = BrightWhite, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Kanallar", color = BrightWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("${channels.size}", color = BrightWhite.copy(.7f), fontSize = 13.sp)
                }
            }
            LazyRow(modifier = Modifier.fillMaxWidth().background(SurfaceBlack).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(groups) { g ->
                    val sel = g == sg
                    Surface(modifier = Modifier.clickable { vm.setGroup(g) }, shape = RoundedCornerShape(20.dp),
                        color = if (sel) CrimsonRed else CardBlack, border = if (!sel) BorderStroke(1.dp, DarkGray) else null) {
                        Text(g, modifier = Modifier.padding(10.dp, 6.dp), color = if (sel) BrightWhite else SubtleGray, fontSize = 12.sp)
                    }
                }
            }
            LazyColumn(state = ls, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                itemsIndexed(channels) { i, ch ->
                    val sel = i == ci
                    val bg by animateColorAsState(if (sel) CrimsonRed.copy(.3f) else Color.Transparent, label = "bg")
                    Surface(modifier = Modifier.fillMaxWidth().clickable { vm.select(i) },
                        shape = RoundedCornerShape(12.dp), color = bg,
                        border = if (sel) BorderStroke(1.dp, CrimsonRed) else null) {
                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${i+1}", color = if (sel) CrimsonRed else SubtleGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                            if (ch.logo.isNotEmpty()) AsyncImage(ch.logo, ch.name, modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(CardBlack), contentScale = ContentScale.Fit)
                            else Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(CardBlack), contentAlignment = Alignment.Center) { Icon(Icons.Default.Tv, null, tint = SubtleGray, modifier = Modifier.size(18.dp)) }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(ch.name, color = if (sel) BrightWhite else DimWhite, fontSize = 14.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(ch.group, color = SubtleGray, fontSize = 11.sp)
                            }
                            if (ch.isAddon) Surface(shape = RoundedCornerShape(4.dp), color = AccentRed.copy(.2f)) {
                                Text("A", Modifier.padding(6.dp, 2.dp), color = AccentRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
