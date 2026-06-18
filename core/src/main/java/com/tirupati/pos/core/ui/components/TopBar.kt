package com.tirupati.pos.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.tirupati.pos.core.R
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.tirupati.pos.core.sync.SyncUiState
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Sync

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    shopName: String,
    modifier: Modifier = Modifier,
    syncUiState: SyncUiState? = null,
    onSyncClicked: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
    onNotificationsClicked: () -> Unit = {},
    onOverflowClicked: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App logo loaded from local drawable resource
                Image(
                    painter = painterResource(id = R.drawable.logo_new),
                    contentDescription = "TEPOS Logo",
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = shopName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            if (syncUiState != null) {
                IconButton(onClick = onSyncClicked, modifier = Modifier.semantics { contentDescription = "Sync Status" }) {
                    val icon = if (!syncUiState.isNetworkAvailable) {
                        Icons.Default.CloudOff
                    } else if (syncUiState.isSyncing) {
                        Icons.Default.Sync
                    } else if (syncUiState.pendingOperationsCount > 0) {
                        Icons.Default.CloudSync
                    } else {
                        Icons.Default.CloudQueue
                    }
                    val tint = if (!syncUiState.isNetworkAvailable) {
                        MaterialTheme.colorScheme.error
                    } else if (syncUiState.isSyncing) {
                        MaterialTheme.colorScheme.primary
                    } else if (syncUiState.pendingOperationsCount > 0) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint
                    )
                }
            }
            IconButton(onClick = onSearchClicked, modifier = Modifier.semantics { contentDescription = "Search" }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onNotificationsClicked, modifier = Modifier.semantics { contentDescription = "Notifications" }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onOverflowClicked, modifier = Modifier.semantics { contentDescription = "More" }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    )
}
