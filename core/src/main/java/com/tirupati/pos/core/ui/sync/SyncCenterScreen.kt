package com.tirupati.pos.core.ui.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tirupati.pos.core.sync.SyncUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncCenterScreen(
    onNavigateBack: () -> Unit,
    viewModel: SyncCenterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Center") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SyncStatusOverviewCard(uiState = uiState)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.requestSync() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isNetworkAvailable && !uiState.isSyncing
            ) {
                Icon(Icons.Default.Sync, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (uiState.isSyncing) "Syncing..." else "Sync Now")
            }
        }
    }
}

@Composable
fun SyncStatusOverviewCard(uiState: SyncUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Overall Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            StatusRow(
                label = "Network Connectivity",
                isOk = uiState.isNetworkAvailable,
                okText = "Connected",
                errorText = "Offline"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            StatusRow(
                label = "Backend Reachability",
                isOk = uiState.isBackendReachable,
                okText = "Reachable",
                errorText = "Unreachable"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val operationsPending = uiState.pendingOperationsCount > 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pending Operations", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${uiState.pendingOperationsCount}",
                    color = if (operationsPending) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val format = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
            val lastSyncStr = if (uiState.lastSyncTime > 0) {
                format.format(Date(uiState.lastSyncTime))
            } else {
                "Never"
            }

            Text(
                text = "Last Synced: $lastSyncStr",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusRow(label: String, isOk: Boolean, okText: String, errorText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isOk) Color.Green else Color.Red)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isOk) okText else errorText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
