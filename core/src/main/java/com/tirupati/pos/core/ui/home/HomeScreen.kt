package com.tirupati.pos.core.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.ui.components.HomeActionCard
import com.tirupati.pos.core.ui.components.RecentActivityCard
import com.tirupati.pos.core.ui.components.SectionTitle
import com.tirupati.pos.core.ui.components.ShopInfoCard
import com.tirupati.pos.core.ui.components.TopBar
import com.tirupati.pos.core.ui.layout.ActionsGrid
import com.tirupati.pos.core.ui.layout.calculateWindowWidthSizeClass
import com.tirupati.pos.core.ui.layout.WindowWidthSizeClass

@Composable
fun HomeScreen(
    state: HomeUiState = HomeUiState(),
    onActionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val widthClass = calculateWindowWidthSizeClass()
    val columns = when (widthClass) {
        WindowWidthSizeClass.Compact -> 2
        WindowWidthSizeClass.Medium -> 3
        WindowWidthSizeClass.Expanded -> 4
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp)) {
            item {
                TopBar(shopName = state.shopName)
            }

            item {
                ShopInfoCard(
                    shopName = state.shopName,
                    date = state.currentDate,
                    time = state.currentTime,
                    internetStatus = state.internetStatus,
                    syncStatus = state.syncStatus,
                    pending = state.pendingSyncCount,
                    lastSync = state.lastSync,
                    printerStatus = state.printerStatus,
                    scannerStatus = state.scannerStatus,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            item {
                SectionTitle(text = "Quick Actions", modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }

            item {
                ActionsGrid(actions = state.quickActions, columns = columns, modifier = Modifier) { action ->
                    HomeActionCard(action = action, onClick = { onActionClick(action.route) })
                }
            }

            item {
                SectionTitle(text = "Recent", modifier = Modifier.padding(top = 12.dp))
            }

            item {
                RecentActivityCard(activities = state.recentActivities, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
