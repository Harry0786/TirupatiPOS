package com.tirupati.pos.feature.sales.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.sync.SyncUiState

import androidx.compose.material.icons.filled.Refresh

@Composable
fun DashboardSyncCard(
    syncUiState: SyncUiState,
    onSyncClicked: () -> Unit,
    onRefreshConnectionClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = if (!syncUiState.isNetworkAvailable) {
                    Icons.Default.CloudOff
                } else if (syncUiState.isSyncing) {
                    Icons.Default.Sync
                } else if (syncUiState.pendingOperationsCount > 0) {
                    Icons.Default.CloudQueue
                } else {
                    Icons.Default.CloudDone
                }

                val tint = if (!syncUiState.isNetworkAvailable) {
                    MaterialTheme.colorScheme.error
                } else if (syncUiState.isSyncing) {
                    MaterialTheme.colorScheme.primary
                } else if (syncUiState.pendingOperationsCount > 0) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.primary
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Sync Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (!syncUiState.isNetworkAvailable) "Offline"
                            else if (syncUiState.isSyncing) "Syncing..."
                            else if (syncUiState.pendingOperationsCount > 0) "${syncUiState.pendingOperationsCount} pending operations"
                            else "Up to date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onRefreshConnectionClicked,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Connection",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onSyncClicked,
                enabled = syncUiState.isNetworkAvailable && !syncUiState.isSyncing,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Sync Now")
            }
        }
    }
}
