package com.tirupati.pos.feature.sales.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.ui.layout.WindowWidthSizeClass
import com.tirupati.pos.core.ui.layout.calculateWindowWidthSizeClass
import com.tirupati.pos.feature.sales.domain.model.Product
import com.tirupati.pos.feature.sales.presentation.component.*
import com.tirupati.pos.feature.sales.presentation.state.EstimateEvent
import com.tirupati.pos.feature.sales.presentation.viewmodel.EstimateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEstimateScreen(
    viewModel: EstimateViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val widthClass = calculateWindowWidthSizeClass()
    val isTablet = widthClass != WindowWidthSizeClass.Compact

    // Local static placeholders for header info
    val currentDate = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()) }
    val currentTime = remember { SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Sales Estimate", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isTablet) {
                // TABLET 30% / 70% LAYOUT
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left Search Panel (30%)
                    ProductSearchPanel(
                        query = state.searchProductsQuery,
                        onQueryChange = { viewModel.onEvent(EstimateEvent.SearchProducts(it)) },
                        results = state.searchProductsResults,
                        onProductClick = { viewModel.onEvent(EstimateEvent.AddProductToEstimate(it)) },
                        onCreateProductClick = { viewModel.onEvent(EstimateEvent.ClickCreateProduct) },
                        modifier = Modifier.weight(1f)
                    )

                    // Right Workspace Panel (70%)
                    Column(
                        modifier = Modifier
                            .weight(2.3f)
                            .fillMaxHeight()
                    ) {
                        EstimateHeaderCard(
                            estimateNumber = "EST-2026-XXXXXX",
                            customerName = "Walk-In Customer",
                            date = currentDate,
                            time = currentTime,
                            status = "Draft"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Estimate Items Table
                        EstimateTable(
                            items = state.selectedItems,
                            onRowClick = { viewModel.onEvent(EstimateEvent.EditItemClicked(it)) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Summary and Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            EstimateSummaryCard(
                                itemsCount = state.selectedItems.size,
                                subtotal = state.subtotal,
                                itemDiscount = state.itemDiscount,
                                billDiscount = state.billDiscount,
                                gstTotal = state.gstTotal,
                                grandTotal = state.grandTotal,
                                onBillDiscountChange = { viewModel.onEvent(EstimateEvent.SetBillDiscount(it)) },
                                modifier = Modifier.weight(1.3f)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.onEvent(EstimateEvent.SaveAndConvertToInvoice) },
                                    enabled = state.selectedItems.isNotEmpty(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Convert to Invoice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.onEvent(EstimateEvent.SaveDraftEstimate) },
                                    enabled = state.selectedItems.isNotEmpty(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Save Draft", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            } else {
                // PHONE SINGLE COLUMN LAYOUT
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Search box
                    OutlinedTextField(
                        value = state.searchProductsQuery,
                        onValueChange = { viewModel.onEvent(EstimateEvent.SearchProducts(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search product to add...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (state.searchProductsQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onEvent(EstimateEvent.SearchProducts("")) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (state.searchProductsQuery.isNotEmpty()) {
                            // Show overlayed product search results list
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Product Results", style = MaterialTheme.typography.titleSmall)
                                        TextButton(onClick = { viewModel.onEvent(EstimateEvent.ClickCreateProduct) }) {
                                            Text("Quick Create")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(state.searchProductsResults) { product ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.onEvent(EstimateEvent.AddProductToEstimate(product))
                                                        viewModel.onEvent(EstimateEvent.SearchProducts(""))
                                                    }
                                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(product.itemName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(product.itemCode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text("₹${product.sellingPrice}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text("GST ${product.gstPercent.toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Show main Estimate list
                                EstimateTable(
                                    items = state.selectedItems,
                                    onRowClick = { viewModel.onEvent(EstimateEvent.EditItemClicked(it)) },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Summary
                                EstimateSummaryCard(
                                    itemsCount = state.selectedItems.size,
                                    subtotal = state.subtotal,
                                    itemDiscount = state.itemDiscount,
                                    billDiscount = state.billDiscount,
                                    gstTotal = state.gstTotal,
                                    grandTotal = state.grandTotal,
                                    onBillDiscountChange = { viewModel.onEvent(EstimateEvent.SetBillDiscount(it)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.onEvent(EstimateEvent.SaveAndConvertToInvoice) },
                        enabled = state.selectedItems.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Convert to Invoice", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.onEvent(EstimateEvent.SaveDraftEstimate) },
                        enabled = state.selectedItems.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Draft", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // BOTTOM SHEET ROW EDITOR
            if (state.showRowEditorBottomSheet && state.activeItemForEditing != null) {
                EstimateItemBottomSheet(
                    item = state.activeItemForEditing!!,
                    onDismiss = { viewModel.onEvent(EstimateEvent.DismissRowEditor) },
                    onSave = { qty, rt, dPct, dAmt, gst ->
                        viewModel.onEvent(
                            EstimateEvent.UpdateItemInEstimate(
                                itemId = state.activeItemForEditing!!.id,
                                quantity = qty,
                                rate = rt,
                                discountPercent = dPct,
                                discountAmount = dAmt,
                                gstPercent = gst
                            )
                        )
                    },
                    onDelete = {
                        viewModel.onEvent(EstimateEvent.RemoveItemFromEstimate(state.activeItemForEditing!!.id))
                    }
                )
            }

            // QUICK CREATE PRODUCT DIALOG
            if (state.showCreateProductDialog) {
                QuickCreateProductDialog(
                    itemCode = state.quickProductItemCode,
                    onItemCodeChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductItemCode(it)) },
                    itemName = state.quickProductItemName,
                    onItemNameChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductItemName(it)) },
                    unit = state.quickProductUnit,
                    onUnitChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductUnit(it)) },
                    sellingPrice = state.quickProductSellingPrice,
                    onSellingPriceChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductSellingPrice(it)) },
                    gstPercent = state.quickProductGstPercent,
                    onGstPercentChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductGstPercent(it)) },
                    onSave = { viewModel.onEvent(EstimateEvent.SaveQuickProduct) },
                    onDismiss = { viewModel.onEvent(EstimateEvent.DismissQuickCreateProductDialog) }
                )
            }
        }
    }
}
