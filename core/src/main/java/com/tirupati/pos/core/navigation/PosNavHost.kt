package com.tirupati.pos.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun PosNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestination.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppDestination.Dashboard.route) { PlaceholderDestination() }
        composable(AppDestination.Auth.route) { PlaceholderDestination() }
        composable(AppDestination.Products.route) { PlaceholderDestination() }
        composable(AppDestination.Billing.route) { PlaceholderDestination() }
        composable(AppDestination.Inventory.route) { PlaceholderDestination() }
        composable(AppDestination.Customers.route) { PlaceholderDestination() }
        composable(AppDestination.Suppliers.route) { PlaceholderDestination() }
        composable(AppDestination.Reports.route) { PlaceholderDestination() }
        composable(AppDestination.Settings.route) { PlaceholderDestination() }
    }
}
