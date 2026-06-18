package com.tirupati.pos.core.ui.home

import com.tirupati.pos.core.database.PendingOperation

data class HomeUiState(
    val shopName: String = "TEPOS",
    val currentDate: String = "18 June 2026",
    val currentTime: String = "10:45 AM",
    val internetStatus: String = "Online",
    val syncStatus: String = "Idle",
    val pendingSyncCount: Int = 0,
    val lastSync: String = "Just now",
    val printerStatus: String = "Connected",
    val scannerStatus: String = "Connected",
    val recentActivities: List<String> = listOf(
        "Sale #1001",
        "Purchase #201",
        "Product Added",
        "Stock Updated"
    ),
    val quickActions: List<HomeAction> = defaultActions()
)

data class HomeAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: Int? = null,
    val route: String
)

private fun defaultActions(): List<HomeAction> = listOf(
    HomeAction("new_sale", "New Sale", "Create new bill", null, "billing"),
    HomeAction("products", "Products", "Manage products", null, "products"),
    HomeAction("purchases", "Purchases", "Purchase stock", null, "purchases"),
    HomeAction("inventory", "Inventory", "Manage stock", null, "inventory"),
    HomeAction("customers", "Customers", "Manage customers", null, "customers"),
    HomeAction("suppliers", "Suppliers", "Manage suppliers", null, "suppliers"),
    HomeAction("reports", "Reports", "View reports", null, "reports"),
    HomeAction("settings", "Settings", "Application settings", null, "settings")
)
