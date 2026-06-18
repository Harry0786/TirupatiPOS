package com.tirupati.pos.core.ui.home

import kotlinx.coroutines.flow.Flow

interface HomeDataProvider {
    fun observeSalesForDate(date: String): Flow<Double>
    fun observeBillsCountForDate(date: String): Flow<Int>
    fun observeItemsSoldCountForDate(date: String): Flow<Int>
    fun observeCustomersCountForDate(date: String): Flow<Int>
    fun observeRecentActivities(limit: Int = 5): Flow<List<RecentActivityItem>>
}
