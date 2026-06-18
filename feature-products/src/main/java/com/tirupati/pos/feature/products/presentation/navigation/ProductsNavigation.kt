package com.tirupati.pos.feature.products.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tirupati.pos.core.navigation.AppDestination
import com.tirupati.pos.feature.products.presentation.screen.*
import com.tirupati.pos.feature.products.presentation.viewmodel.ProductsViewModel

fun NavGraphBuilder.productsGraph(
    navController: NavController,
    productsViewModel: ProductsViewModel
) {
    navigation(
        startDestination = "products_dashboard",
        route = AppDestination.Products.route
    ) {
        composable("products_dashboard") {
            ProductsDashboardScreen(
                viewModel = productsViewModel,
                onAddCompanyClick = { navController.navigate("add_company") },
                onAddProductClick = { navController.navigate("add_product") },
                onProductClick = { productId -> navController.navigate("product_details/$productId") },
                onEditProductClick = { productId -> navController.navigate("edit_product/$productId") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("add_company") {
            AddCompanyScreen(
                viewModel = productsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("add_product") {
            AddProductScreen(
                viewModel = productsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("product_details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailsScreen(
                productId = productId,
                viewModel = productsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = { navController.navigate("edit_product/$productId") }
            )
        }
        composable("edit_product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            EditProductScreen(
                productId = productId,
                viewModel = productsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
