package com.tirupati.pos.feature.sales.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateStatus
import com.tirupati.pos.feature.sales.presentation.component.EstimateHeaderCard
import com.tirupati.pos.feature.sales.presentation.component.EstimateSummaryCard
import com.tirupati.pos.feature.sales.presentation.component.TableCell
import com.tirupati.pos.feature.sales.presentation.state.EstimateEvent
import com.tirupati.pos.feature.sales.presentation.viewmodel.EstimateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimateDetailScreen(
    estimateId: String,
    viewModel: EstimateViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(estimateId) {
        viewModel.loadEstimateDetails(estimateId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estimate Details", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            val currentEstimate = state.currentEstimate
            if (currentEstimate != null) {
                EstimateDetailContent(
                    estimate = currentEstimate,
                    onConvertToInvoiceClick = {
                        viewModel.onEvent(EstimateEvent.ConvertToInvoice(it))
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun EstimateDetailContent(
    estimate: Estimate,
    onConvertToInvoiceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Card
        EstimateHeaderCard(
            estimateNumber = estimate.estimateNumber,
            customerName = estimate.customerName,
            date = estimate.date,
            time = estimate.time,
            status = estimate.status.name
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Table
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState)
            ) {
                // Header row
                Row(
                    modifier = Modifier
                        .width(900.dp)
                        .background(Color(0xFFF9FAFB))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "Sr No", weight = 0.8f, isHeader = true)
                    TableCell(text = "Item Code", weight = 1.5f, isHeader = true)
                    TableCell(text = "Item Name", weight = 2.5f, isHeader = true)
                    TableCell(text = "Qty", weight = 1.0f, isHeader = true, alignRight = true)
                    TableCell(text = "Unit", weight = 1.0f, isHeader = true)
                    TableCell(text = "Rate (₹)", weight = 1.2f, isHeader = true, alignRight = true)
                    TableCell(text = "Disc %", weight = 1.2f, isHeader = true, alignRight = true)
                    TableCell(text = "GST %", weight = 1.0f, isHeader = true, alignRight = true)
                    TableCell(text = "Amount (₹)", weight = 1.6f, isHeader = true, alignRight = true)
                }

                HorizontalDivider(color = Color(0xFFE5E7EB))

                LazyColumn(modifier = Modifier.width(900.dp)) {
                    items(estimate.items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 0.5.dp, color = Color(0xFFF3F4F6))
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = item.srNo.toString(), weight = 0.8f)
                            TableCell(text = item.itemCode, weight = 1.5f)
                            TableCell(text = item.itemName, weight = 2.5f)
                            TableCell(text = item.quantity.toString(), weight = 1.0f, alignRight = true)
                            TableCell(text = item.unit, weight = 1.0f)
                            TableCell(text = String.format("%.2f", item.rate), weight = 1.2f, alignRight = true)
                            TableCell(text = String.format("%.1f", item.discountPercent), weight = 1.2f, alignRight = true)
                            TableCell(text = "${item.gstPercent.toInt()}%", weight = 1.0f, alignRight = true)
                            TableCell(text = String.format("%.2f", item.amount), weight = 1.6f, alignRight = true)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Card & Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            EstimateSummaryCard(
                itemsCount = estimate.items.size,
                subtotal = estimate.subtotal,
                itemDiscount = estimate.itemDiscount,
                billDiscount = estimate.billDiscount,
                gstTotal = estimate.gstTotal,
                grandTotal = estimate.grandTotal,
                onBillDiscountChange = {},
                editable = false,
                modifier = Modifier.weight(1.3f)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val canConvert = estimate.status == EstimateStatus.DRAFT || estimate.status == EstimateStatus.APPROVED
                Button(
                    onClick = { onConvertToInvoiceClick(estimate.id) },
                    enabled = canConvert,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (canConvert) "Convert to Invoice" else "Already Converted",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
