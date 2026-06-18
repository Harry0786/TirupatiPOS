package com.tirupati.pos.core.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.ui.components.HomeActionCard
import com.tirupati.pos.core.ui.components.RecentActivityCard
import com.tirupati.pos.core.ui.components.SectionTitle
import com.tirupati.pos.core.ui.components.ShopInfoCard
import com.tirupati.pos.core.ui.components.TodaysSummaryCard
import com.tirupati.pos.core.ui.components.TopBar
import com.tirupati.pos.core.ui.layout.calculateWindowWidthSizeClass
import com.tirupati.pos.core.ui.layout.WindowWidthSizeClass
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    state: HomeUiState = HomeUiState(),
    onActionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val widthClass = calculateWindowWidthSizeClass()
    val isTablet = widthClass != WindowWidthSizeClass.Compact
    
    // Ticking digital clock states
    var currentTime by remember { mutableStateOf(state.currentTime) }
    var currentDate by remember { mutableStateOf(state.currentDate) }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }
    
    // 4 columns on tablets, 2 columns on phones
    val columns = if (isTablet) 4 else 2

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                TopBar(
                    shopName = state.shopName,
                    modifier = Modifier
                        .widthIn(max = 1600.dp)
                        .padding(horizontal = 8.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1600.dp)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Redesigned Dashboard Header Card (greeting, time, status)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ShopInfoCard(
                        greeting = state.greeting,
                        date = currentDate,
                        time = currentTime,
                        internetStatus = state.internetStatus,
                        syncStatus = state.syncStatus,
                        pending = state.pendingSyncCount,
                        lastSync = state.lastSync,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                // 2. Quick Actions Section Header
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(16.dp)) // 16dp spacer + 16dp grid arrangement = 32dp visual gap
                    SectionTitle(text = "Quick Actions")
                }

                // 3. Grid of Quick Actions cards (4 columns in tablet, 2 in phone)
                items(state.quickActions) { action ->
                    HomeActionCard(
                        action = action,
                        onClick = { onActionClick(action.route) }
                    )
                }

                // 4. Bottom Row containing Recent Activity & Today's Summary
                if (isTablet) {
                    // Side-by-Side on Tablet Landscape Mode
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Left Column: Recent Activity (60% width equivalent)
                            Column(modifier = Modifier.weight(1.3f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SectionTitle(text = "Recent Activity")
                                    Text(
                                        text = "View all",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.clickable { /* Navigation */ }
                                    )
                                }
                                RecentActivityCard(activities = state.recentActivities)
                            }
                            
                            // Right Column: Today's Summary (40% width equivalent)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SectionTitle(text = "Today's Summary")
                                }
                                TodaysSummaryCard(
                                    sales = state.salesToday,
                                    bills = state.billsToday,
                                    customers = state.customersCount,
                                    itemsSold = state.itemsSoldCount
                                )
                            }
                        }
                    }
                } else {
                    // Vertically stacked on Phone Mode to prevent clutter and preserve visual flow
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle(text = "Recent Activity")
                            Text(
                                text = "View all",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.clickable { /* Navigation */ }
                            )
                        }
                    }
                    
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        RecentActivityCard(activities = state.recentActivities)
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle(text = "Today's Summary")
                        }
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TodaysSummaryCard(
                            sales = state.salesToday,
                            bills = state.billsToday,
                            customers = state.customersCount,
                            itemsSold = state.itemsSoldCount
                        )
                    }
                }
            }
        }
    }
}
