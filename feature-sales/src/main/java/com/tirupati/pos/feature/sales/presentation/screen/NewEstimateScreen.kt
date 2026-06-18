package com.tirupati.pos.feature.sales.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tirupati.pos.core.ui.layout.WindowWidthSizeClass
import com.tirupati.pos.core.ui.layout.calculateWindowWidthSizeClass
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.presentation.component.*
import com.tirupati.pos.feature.sales.presentation.state.EstimateEvent
import com.tirupati.pos.feature.sales.presentation.viewmodel.EstimateViewModel

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

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearActiveEstimate()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estimate Editor", fontWeight = FontWeight.Bold) },
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
            val activeEstimate = state.currentEstimate
            if (activeEstimate == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (isTablet) {
                    // TABLET 3-COLUMN LAYOUT: Search (25%), Editor Table (50%), Row Edit Panel (25%)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. Left Search Panel (25% weight)
                        ProductSearchPanel(
                            query = state.searchProductsQuery,
                            onQueryChange = { viewModel.onEvent(EstimateEvent.SearchProducts(it)) },
                            results = state.searchProductsResults,
                            onProductClick = { viewModel.onEvent(EstimateEvent.AddProductToEstimate(it)) },
                            onCreateProductClick = { viewModel.onEvent(EstimateEvent.ClickCreateProduct) },
                            modifier = Modifier.weight(1f)
                        )

                        // 2. Center Workspace Panel (50% or 75% weight depending on Side Panel visibility)
                        val workspaceWeight = if (state.activeItemForEditing != null) 2f else 3f
                        Column(
                            modifier = Modifier
                                .weight(workspaceWeight)
                                .fillMaxHeight()
                        ) {
                            EstimateHeaderCard(
                                estimateNumber = activeEstimate.estimateNumber,
                                customerName = activeEstimate.customerName,
                                date = activeEstimate.date,
                                time = activeEstimate.time,
                                status = activeEstimate.status.name
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Estimate Items Table
                            EstimateTable(
                                items = state.selectedItems,
                                onRowClick = { viewModel.onEvent(EstimateEvent.EditItemClicked(it)) },
                                stockWarnings = state.stockWarnings,
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
                                    discountTotal = state.discountTotal,
                                    gstTotal = state.gstTotal,
                                    grandTotal = state.grandTotal,
                                    modifier = Modifier.weight(1.3f)
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(bottom = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Primary Action: Print Estimate
                                    Button(
                                        onClick = { viewModel.onEvent(EstimateEvent.ClickPrintEstimate) },
                                        enabled = state.selectedItems.isNotEmpty(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Print, contentDescription = "Print")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Print Estimate", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.onEvent(EstimateEvent.SaveAndConvertToInvoice) },
                                            enabled = state.selectedItems.isNotEmpty(),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Convert to Invoice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.onEvent(EstimateEvent.SaveDraftEstimate) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Save Draft", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Right Side Panel for Row Editing (only visible if activeItemForEditing != null)
                        if (state.activeItemForEditing != null) {
                            EstimateItemSidePanel(
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
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                } else {
                    // PHONE SINGLE COLUMN LAYOUT (fallback for compact viewports)
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
                                                        Text("₹${product.sellingRate}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                    }
                                                }
                                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                            }
                                        }
                                    }
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    EstimateTable(
                                        items = state.selectedItems,
                                        onRowClick = { viewModel.onEvent(EstimateEvent.EditItemClicked(it)) },
                                        stockWarnings = state.stockWarnings,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    EstimateSummaryCard(
                                        itemsCount = state.selectedItems.size,
                                        subtotal = state.subtotal,
                                        discountTotal = state.discountTotal,
                                        gstTotal = state.gstTotal,
                                        grandTotal = state.grandTotal,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.onEvent(EstimateEvent.ClickPrintEstimate) },
                            enabled = state.selectedItems.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Print")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Print Estimate", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.onEvent(EstimateEvent.SaveAndConvertToInvoice) },
                                enabled = state.selectedItems.isNotEmpty(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Invoice", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.onEvent(EstimateEvent.SaveDraftEstimate) },
                                enabled = state.selectedItems.isNotEmpty(),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save Draft", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // BOTTOM SHEET ROW EDITOR (compact viewports only)
                if (!isTablet && state.showRowEditorBottomSheet && state.activeItemForEditing != null) {
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
            }

            // QUICK CREATE PRODUCT DIALOG
            if (state.showCreateProductDialog) {
                QuickCreateProductDialog(
                    companies = state.companies,
                    selectedCompanyId = state.quickProductCompanyId,
                    onCompanyIdChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductCompanyId(it)) },
                    onAddCompanyClick = { viewModel.onEvent(EstimateEvent.ClickCreateCompany) },
                    itemCode = state.quickProductItemCode,
                    onItemCodeChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductItemCode(it)) },
                    itemName = state.quickProductItemName,
                    onItemNameChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductItemName(it)) },
                    unit = state.quickProductUnit,
                    onUnitChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductUnit(it)) },
                    purchaseRate = state.quickProductPurchaseRate,
                    onPurchaseRateChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductPurchaseRate(it)) },
                    sellingPrice = state.quickProductSellingPrice,
                    onSellingPriceChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductSellingPrice(it)) },
                    stockQuantity = state.quickProductStockQuantity,
                    onStockQuantityChange = { viewModel.onEvent(EstimateEvent.UpdateQuickProductStockQuantity(it)) },
                    onSave = { viewModel.onEvent(EstimateEvent.SaveQuickProduct) },
                    onDismiss = { viewModel.onEvent(EstimateEvent.DismissQuickCreateProductDialog) }
                )
            }

            // DUPLICATE PRODUCT WARNING DIALOG
            if (state.showDuplicateProductWarning) {
                DuplicateProductWarningDialog(
                    message = state.duplicateProductWarningMessage,
                    onConfirm = { viewModel.onEvent(EstimateEvent.ConfirmSaveDuplicateProduct) },
                    onDismiss = { viewModel.onEvent(EstimateEvent.DismissDuplicateProductWarning) }
                )
            }

            // QUICK CREATE COMPANY DIALOG
            if (state.showCreateCompanyDialog) {
                QuickCreateCompanyDialog(
                    name = state.quickCompanyName,
                    onNameChange = { viewModel.onEvent(EstimateEvent.UpdateQuickCompanyName(it)) },
                    onSave = { viewModel.onEvent(EstimateEvent.SaveQuickCompany) },
                    onDismiss = { viewModel.onEvent(EstimateEvent.DismissQuickCreateCompanyDialog) }
                )
            }

            // PRINT DIALOG
            if (state.showPrintDialog) {
                PrintEstimateDialog(
                    customerName = state.printCustomerName,
                    onCustomerNameChange = { viewModel.onEvent(EstimateEvent.UpdatePrintCustomerName(it)) },
                    phoneNumber = state.printPhoneNumber,
                    onPhoneNumberChange = { viewModel.onEvent(EstimateEvent.UpdatePrintPhoneNumber(it)) },
                    address = state.printAddress,
                    onAddressChange = { viewModel.onEvent(EstimateEvent.UpdatePrintAddress(it)) },
                    onConfirmPrint = { viewModel.onEvent(EstimateEvent.ConfirmPrintEstimate) },
                    onSkipPrint = {
                        viewModel.onEvent(EstimateEvent.UpdatePrintCustomerName("Walk-In Customer"))
                        viewModel.onEvent(EstimateEvent.ConfirmPrintEstimate)
                    },
                    onDismiss = { viewModel.onEvent(EstimateEvent.DismissPrintDialog) }
                )
            }

            // PRINT PREVIEW RECEIPT
            if (state.showPrintPreview && activeEstimate != null) {
                PrintPreviewDialog(
                    estimate = activeEstimate,
                    items = state.selectedItems,
                    customerPhone = state.printPhoneNumber,
                    customerAddress = state.printAddress,
                    onClose = { viewModel.onEvent(EstimateEvent.ClosePrintPreview) }
                )
            }
        }
    }
}

@Composable
fun EstimateItemSidePanel(
    item: EstimateItem,
    onDismiss: () -> Unit,
    onSave: (quantity: Int, rate: Double, discountPercent: Double, discountAmount: Double, gstPercent: Double) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by remember(item.id) { mutableStateOf(item.quantity.toString()) }
    var rate by remember(item.id) { mutableStateOf(item.sellingRate.toString()) }
    var discPercent by remember(item.id) { mutableStateOf(item.discountPercent.toString()) }
    var discAmount by remember(item.id) { mutableStateOf(item.discountAmount.toString()) }
    var gstPercent by remember(item.id) { mutableStateOf(item.gstPercent) }

    val qtyVal = quantity.toIntOrNull() ?: 0
    val rateVal = rate.toDoubleOrNull() ?: 0.0
    val discPctVal = discPercent.toDoubleOrNull() ?: 0.0
    val discAmtVal = discAmount.toDoubleOrNull() ?: 0.0
    
    val lineTotal = remember(qtyVal, rateVal, discPctVal, discAmtVal, gstPercent) {
        val raw = qtyVal * rateVal
        val pDisc = raw * (discPctVal / 100.0)
        val taxable = (raw - pDisc - discAmtVal).coerceAtLeast(0.0)
        taxable * (1 + gstPercent / 100.0)
    }

    Card(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Item",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Panel")
                }
            }
            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Code: ${item.itemCode} | Unit: ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rate,
                onValueChange = { rate = it },
                label = { Text("Rate (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = discPercent,
                    onValueChange = { discPercent = it },
                    label = { Text("Disc %") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = discAmount,
                    onValueChange = { discAmount = it },
                    label = { Text("Disc ₹") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GST Rate",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(0.0, 5.0, 12.0, 18.0, 28.0).forEach { rateVal ->
                    val isSelected = gstPercent == rateVal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF3F4F6))
                            .clickable { gstPercent = rateVal }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${rateVal.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF374151)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Line Total:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = String.format("₹%.2f", lineTotal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        val qty = quantity.toIntOrNull() ?: 0
                        val rt = rate.toDoubleOrNull() ?: 0.0
                        val dPct = discPercent.toDoubleOrNull() ?: 0.0
                        val dAmt = discAmount.toDoubleOrNull() ?: 0.0
                        onSave(qty, rt, dPct, dAmt, gstPercent)
                    },
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
