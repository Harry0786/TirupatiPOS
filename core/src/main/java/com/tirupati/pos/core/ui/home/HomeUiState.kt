package com.tirupati.pos.core.ui.home

data class HomeUiState(
    val shopName: String = "TEPOS",
    val greeting: String = "Good Afternoon, Jayesh",
    val currentDate: String = "Thursday, 18 June 2026",
    val currentTime: String = "2:30:45 PM",
    
    // Status metrics
    val internetStatus: String = "Online",
    val syncStatus: String = "All Synced",
    val lastSync: String = "2 seconds ago",
    val pendingSyncCount: Int = 0,
    
    // Summary metrics
    val salesToday: String = "₹12,850",
    val salesTodayTrend: String = "18% vs Yesterday",
    val billsToday: Int = 24,
    val billsTodayTrend: String = "9% vs Yesterday",
    val customersCount: Int = 18,
    val customersTrend: String = "4% vs Yesterday",
    val itemsSoldCount: Int = 96,
    val itemsSoldTrend: String = "11% vs Yesterday",
    
    // Recent activities list structured
    val recentActivities: List<RecentActivityItem> = defaultActivities(),
    val quickActions: List<HomeAction> = defaultActions()
)

data class RecentActivityItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val type: String // "sale", "stock", "purchase", "payment"
)

data class HomeAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: Int? = null,
    val route: String
)

private fun defaultActivities(): List<RecentActivityItem> = listOf(
    RecentActivityItem("1", "Invoice INV-1005", "Sale • ₹1,250", "2 min ago", "sale"),
    RecentActivityItem("2", "Stock Updated", "LED Bulb • +40 Qty", "15 min ago", "stock"),
    RecentActivityItem("3", "Purchase Added", "ABC Electronics • ₹18,500", "32 min ago", "purchase"),
    RecentActivityItem("4", "Payment Received", "INV-1004 • ₹2,350", "48 min ago", "payment")
)

private fun defaultActions(): List<HomeAction> = listOf(
    HomeAction("new_estimate", "New Estimate", "Create new estimate", null, "sales/new"),
    HomeAction("estimates_list", "Estimates List", "View saved estimates", null, "billing"),
    HomeAction("products", "Products", "Manage products", null, "products"),
    HomeAction("purchases", "Purchases", "Purchase stock", null, "purchases"),
    HomeAction("inventory", "Inventory", "Manage stock", null, "inventory"),
    HomeAction("customers", "Customers", "Manage customers", null, "customers"),
    HomeAction("settings", "Settings", "Application settings", null, "settings")
)
