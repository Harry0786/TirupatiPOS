package com.tirupati.pos.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirupati.pos.core.navigation.PosNavHost
import com.tirupati.pos.core.theme.AppTheme
import com.tirupati.pos.feature.sales.presentation.navigation.salesGraph
import com.tirupati.pos.feature.sales.presentation.viewmodel.EstimateViewModel

@Composable
fun PosApp() {
    AppTheme {
        val estimateViewModel: EstimateViewModel = hiltViewModel()
        PosNavHost(
            salesGraphBuilder = { builder, controller ->
                builder.salesGraph(controller, estimateViewModel)
            }
        )
    }
}
