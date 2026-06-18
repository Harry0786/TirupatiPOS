package com.tirupati.pos.ui

import androidx.compose.runtime.Composable
import com.tirupati.pos.core.navigation.PosNavHost
import com.tirupati.pos.core.theme.AppTheme

@Composable
fun PosApp() {
    AppTheme {
        PosNavHost()
    }
}
