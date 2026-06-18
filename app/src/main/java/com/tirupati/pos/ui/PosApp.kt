package com.tirupati.pos.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirupati.pos.core.navigation.PosNavHost
import com.tirupati.pos.core.theme.AppTheme
import com.tirupati.pos.feature.sales.presentation.navigation.salesGraph
import com.tirupati.pos.feature.sales.presentation.viewmodel.EstimateViewModel

import com.tirupati.pos.feature.products.presentation.navigation.productsGraph
import com.tirupati.pos.feature.products.presentation.viewmodel.ProductsViewModel

@Composable
fun PosApp() {
    AppTheme {
        val estimateViewModel: EstimateViewModel = hiltViewModel()
        val productsViewModel: ProductsViewModel = hiltViewModel()
        
        PosNavHost(
            salesGraphBuilder = { builder, nav -> builder.salesGraph(nav, estimateViewModel) },
            productsGraphBuilder = { builder, nav -> builder.productsGraph(nav, productsViewModel) }
        )
    }
}
