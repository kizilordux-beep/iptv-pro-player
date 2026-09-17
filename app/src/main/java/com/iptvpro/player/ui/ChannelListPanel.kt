package com.iptvpro.player.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iptvpro.player.viewmodel.PlayerViewModel

@Composable
fun ChannelListPanel(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val filtered by viewModel.filtered.collectAsState()
    val idx by viewModel.idx.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val group by viewModel.group.collectAsState()

    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Kategoriler ($group)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn {
                itemsIndexed(filtered) { index, channel ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.select(channel) },
                        color = if (index == idx) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "${index + 1}. ${channel.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
