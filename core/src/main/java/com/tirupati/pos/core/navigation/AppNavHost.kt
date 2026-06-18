package com.tirupati.pos.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = AppDestination.Dashboard.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestination.Dashboard.route) { PlaceholderDestination("Dashboard") }
        composable(AppDestination.Auth.route) { PlaceholderDestination("Auth") }
        composable(AppDestination.Products.route) { PlaceholderDestination("Products") }
        composable(AppDestination.Billing.route) { PlaceholderDestination("Billing") }
        composable(AppDestination.Inventory.route) { PlaceholderDestination("Inventory") }
        composable(AppDestination.Customers.route) { PlaceholderDestination("Customers") }
        composable(AppDestination.Suppliers.route) { PlaceholderDestination("Suppliers") }
        composable(AppDestination.Reports.route) { PlaceholderDestination("Reports") }
        composable(AppDestination.Settings.route) { PlaceholderDestination("Settings") }
    }
}

@Composable
private fun PlaceholderDestination(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$name module placeholder")
    }
}
