package com.tirupati.pos.core.navigation

sealed class AppDestination(val route: String) {
    data object Splash : AppDestination("splash")
    data object Dashboard : AppDestination("dashboard")
    data object Auth : AppDestination("auth")
    data object Products : AppDestination("products")
    data object Billing : AppDestination("billing")
    data object Inventory : AppDestination("inventory")
    data object Customers : AppDestination("customers")
    data object Suppliers : AppDestination("suppliers")
    data object Reports : AppDestination("reports")
    data object Settings : AppDestination("settings")
}
