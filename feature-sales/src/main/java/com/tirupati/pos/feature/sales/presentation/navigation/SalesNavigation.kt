package com.tirupati.pos.feature.sales.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tirupati.pos.feature.sales.presentation.screen.EstimateDetailScreen
import com.tirupati.pos.feature.sales.presentation.screen.EstimatesScreen
import com.tirupati.pos.feature.sales.presentation.screen.NewEstimateScreen
import com.tirupati.pos.feature.sales.presentation.screen.PaymentScreen
import com.tirupati.pos.feature.sales.presentation.state.EstimateEffect
import com.tirupati.pos.feature.sales.presentation.state.EstimateEvent
import com.tirupati.pos.feature.sales.presentation.viewmodel.EstimateViewModel

fun NavGraphBuilder.salesGraph(
    navController: NavController,
    viewModel: EstimateViewModel
) {
    navigation(
        startDestination = "sales/estimates",
        route = "billing" // Intercept "billing" route from the home quick action
    ) {
        composable("sales/estimates") {
            val context = LocalContext.current
            
            // Collect side effects in the main list composable only if it is the active destination
            LaunchedEffect(viewModel.effect) {
                viewModel.effect.collect { effect ->
                    if (navController.currentDestination?.route == "sales/estimates") {
                        when (effect) {
                            is EstimateEffect.NavigateToInvoice -> {
                                navController.navigate("sales/payment/${effect.invoiceId}")
                            }
                            is EstimateEffect.NavigateToPayment -> {
                                navController.navigate("sales/payment/${effect.invoiceId}")
                            }
                            is EstimateEffect.ShowError -> {
                                Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                            }
                            is EstimateEffect.NavigateBack -> {
                                navController.popBackStack()
                            }
                            else -> {}
                        }
                    }
                }
            }

            EstimatesScreen(
                viewModel = viewModel,
                onAddEstimateClick = { navController.navigate("sales/new") },
                onEstimateClick = { id -> navController.navigate("sales/detail/$id") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("sales/new") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                viewModel.onEvent(EstimateEvent.StartNewEstimate)
            }
            LaunchedEffect(viewModel.effect) {
                viewModel.effect.collect { effect ->
                    if (navController.currentDestination?.route == "sales/new") {
                        when (effect) {
                            is EstimateEffect.NavigateToEstimatesList -> {
                                navController.popBackStack()
                            }
                            is EstimateEffect.NavigateToInvoice -> {
                                navController.navigate("sales/payment/${effect.invoiceId}") {
                                    popUpTo("sales/estimates")
                                }
                            }
                            is EstimateEffect.NavigateToPayment -> {
                                navController.navigate("sales/payment/${effect.invoiceId}") {
                                    popUpTo("sales/estimates")
                                }
                            }
                            is EstimateEffect.ShowError -> {
                                Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                            }
                            else -> {}
                        }
                    }
                }
            }

            NewEstimateScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "sales/detail/{estimateId}",
            arguments = listOf(navArgument("estimateId") { type = NavType.StringType })
        ) { backStackEntry ->
            val estimateId = backStackEntry.arguments?.getString("estimateId") ?: ""
            val context = LocalContext.current

            LaunchedEffect(viewModel.effect) {
                viewModel.effect.collect { effect ->
                    if (navController.currentDestination?.route?.startsWith("sales/detail") == true) {
                        when (effect) {
                            is EstimateEffect.NavigateToInvoice -> {
                                navController.navigate("sales/payment/${effect.invoiceId}")
                            }
                            is EstimateEffect.ShowError -> {
                                Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                            }
                            else -> {}
                        }
                    }
                }
            }

            EstimateDetailScreen(
                estimateId = estimateId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "sales/payment/{invoiceId}",
            arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
            PaymentScreen(
                invoiceId = invoiceId,
                viewModel = viewModel,
                onPaymentSuccess = {
                    navController.popBackStack("sales/estimates", false)
                }
            )
        }
    }
}
