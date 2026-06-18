package com.tirupati.pos.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.tirupati.pos.core.navigation.AppNavHost
import com.tirupati.pos.core.theme.AppTheme

@Composable
fun PosApp() {
    val navController = rememberNavController()
    AppTheme {
        AppNavHost(navController = navController)
    }
}
