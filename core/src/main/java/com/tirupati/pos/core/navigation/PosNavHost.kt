package com.tirupati.pos.core.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tirupati.pos.core.R
import com.tirupati.pos.core.ui.home.HomeScreen
import kotlinx.coroutines.delay

@Composable
fun PosNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestination.Splash.route,
    salesGraphBuilder: (NavGraphBuilder, NavController) -> Unit = { builder, controller ->
        builder.composable(AppDestination.Billing.route) { PlaceholderDestination() }
    },
    productsGraphBuilder: (NavGraphBuilder, NavController) -> Unit = { builder, controller ->
        builder.composable(AppDestination.Products.route) { ProductsPlaceholder() }
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppDestination.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(AppDestination.Dashboard.route) {
                    popUpTo(AppDestination.Splash.route) { inclusive = true }
                }
            })
        }
        composable(AppDestination.Dashboard.route) {
            HomeScreen(
                onActionClick = { route -> navController.navigate(route) },
                modifier = Modifier
            )
        }
        composable(AppDestination.Auth.route) { PlaceholderDestination() }
        
        // Invoke the injected products graph builder
        productsGraphBuilder(this, navController)
        
        // Invoke the injected sales graph builder
        salesGraphBuilder(this, navController)
        
        composable(AppDestination.Inventory.route) { PlaceholderDestination() }
        composable(AppDestination.Customers.route) { PlaceholderDestination() }
        composable(AppDestination.Suppliers.route) { PlaceholderDestination() }

        composable(AppDestination.Reports.route) { PlaceholderDestination() }
        composable(AppDestination.Settings.route) { PlaceholderDestination() }
    }
}

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(1200) // Keep the logo visible for 1.2s after fade in
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_new),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(160.dp) // Large centered logo for high visibility
                .alpha(alpha.value)
        )
    }
}

@Composable
fun ProductsPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "Products Screen",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
    }
}
