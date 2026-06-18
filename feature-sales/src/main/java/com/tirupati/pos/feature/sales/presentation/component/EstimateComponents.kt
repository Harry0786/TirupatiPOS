package com.tirupati.pos.feature.sales.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.window.Dialog
import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.Product

@Composable
fun EstimateHeaderCard(
    estimateNumber: String,
    customerName: String,
    date: String,
    time: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = estimateNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = customerName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4B5563)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (status.uppercase()) {
                                    "DRAFT" -> Color(0xFFFEF3C7)
                                    "APPROVED" -> Color(0xFFD1FAE5)
                                    "INVOICE" -> Color(0xFFDBEAFE)
                                    "PAID" -> Color(0xFFE0F2FE)
                                    else -> Color(0xFFF3F4F6)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (status.uppercase()) {
                                "DRAFT" -> Color(0xFFD97706)
                                "APPROVED" -> Color(0xFF059669)
                                "INVOICE" -> Color(0xFF2563EB)
                                "PAID" -> Color(0xFF0284C7)
                                else -> Color(0xFF4B5563)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$date | $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun ProductSearchPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<Product>,
    onProductClick: (Product) -> Unit,
    onCreateProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Product Search",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Code or Name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (results.isEmpty() && query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No Product Found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9CA3AF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onCreateProductClick,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Create Product")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Results",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280)
                    )
                    TextButton(onClick = onCreateProductClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quick Create", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProductClick(product) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = product.itemName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = product.itemCode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF4F46E5),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Rate: ₹${product.sellingPrice}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF4B5563)
                                    )
                                    Text(
                                        text = "Stock: ${product.stock} ${product.unit}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF059669),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstimateTable(
    items: List<EstimateItem>,
    onRowClick: (EstimateItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .width(1000.dp)
                    .background(Color(0xFFF9FAFB))
                    .border(width = 1.dp, color = Color(0xFFE5E7EB), shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = "Sr No", weight = 0.8f, isHeader = true)
                TableCell(text = "Item Code", weight = 1.5f, isHeader = true)
                TableCell(text = "Item Name", weight = 2.5f, isHeader = true)
                TableCell(text = "Qty", weight = 1.0f, isHeader = true, alignRight = true)
                TableCell(text = "Unit", weight = 1.0f, isHeader = true)
                TableCell(text = "Rate (₹)", weight = 1.2f, isHeader = true, alignRight = true)
                TableCell(text = "Disc %", weight = 1.2f, isHeader = true, alignRight = true)
                TableCell(text = "Disc ₹", weight = 1.2f, isHeader = true, alignRight = true)
                TableCell(text = "GST %", weight = 1.0f, isHeader = true, alignRight = true)
                TableCell(text = "Amount (₹)", weight = 1.6f, isHeader = true, alignRight = true)
            }

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .width(1000.dp)
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items added yet. Search products on the left panel to add.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                }
            } else {
                Column(modifier = Modifier.width(1000.dp)) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 0.5.dp, color = Color(0xFFF3F4F6))
                                .clickable { onRowClick(item) }
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
                            TableCell(text = String.format("%.2f", item.discountAmount), weight = 1.2f, alignRight = true)
                            TableCell(text = "${item.gstPercent.toInt()}%", weight = 1.0f, alignRight = true)
                            TableCell(text = String.format("%.2f", item.amount), weight = 1.6f, alignRight = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false,
    alignRight: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = if (isHeader) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) Color(0xFF374151) else Color(0xFF1F2937),
        textAlign = if (alignRight) TextAlign.End else TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun EstimateSummaryCard(
    itemsCount: Int,
    subtotal: Double,
    itemDiscount: Double,
    billDiscount: Double,
    gstTotal: Double,
    grandTotal: Double,
    onBillDiscountChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    editable: Boolean = true
) {
    var discountInput by remember(billDiscount) { mutableStateOf(if (billDiscount > 0) billDiscount.toString() else "") }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            SummaryRow(label = "Items Count", value = itemsCount.toString())
            SummaryRow(label = "Subtotal", value = String.format("₹%.2f", subtotal))
            SummaryRow(label = "Item Discount", value = String.format("- ₹%.2f", itemDiscount), valueColor = Color(0xFFDC2626))
            
            if (editable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bill Discount (Flat)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4B5563)
                    )
                    OutlinedTextField(
                        value = discountInput,
                        onValueChange = {
                            discountInput = it
                            val amt = it.toDoubleOrNull() ?: 0.0
                            onBillDiscountChange(amt)
                        },
                        modifier = Modifier.width(100.dp),
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                }
            } else {
                SummaryRow(label = "Bill Discount", value = String.format("- ₹%.2f", billDiscount), valueColor = Color(0xFFDC2626))
            }
            
            SummaryRow(label = "GST Total", value = String.format("₹%.2f", gstTotal))
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grand Total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = String.format("₹%.2f", grandTotal),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF111827)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF4B5563)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimateItemBottomSheet(
    item: EstimateItem,
    onDismiss: () -> Unit,
    onSave: (quantity: Int, rate: Double, discountPercent: Double, discountAmount: Double, gstPercent: Double) -> Unit,
    onDelete: () -> Unit
) {
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var rate by remember { mutableStateOf(item.rate.toString()) }
    var discPercent by remember { mutableStateOf(item.discountPercent.toString()) }
    var discAmount by remember { mutableStateOf(item.discountAmount.toString()) }
    var gstPercent by remember { mutableStateOf(item.gstPercent) }

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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Edit Item: ${item.itemName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Item Code: ${item.itemCode} | Unit: ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Rate (₹)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = discPercent,
                    onValueChange = { discPercent = it },
                    label = { Text("Discount %") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = discAmount,
                    onValueChange = { discAmount = it },
                    label = { Text("Discount ₹") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GST Rate",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0.0, 5.0, 12.0, 18.0, 28.0).forEach { rateVal ->
                    val isSelected = gstPercent == rateVal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF3F4F6))
                            .clickable { gstPercent = rateVal }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${rateVal.toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
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
                Text(
                    text = "Line Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )
                Text(
                    text = String.format("₹%.2f", lineTotal),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }

                Button(
                    onClick = {
                        val qty = quantity.toIntOrNull() ?: 0
                        val rt = rate.toDoubleOrNull() ?: 0.0
                        val dPct = discPercent.toDoubleOrNull() ?: 0.0
                        val dAmt = discAmount.toDoubleOrNull() ?: 0.0
                        onSave(qty, rt, dPct, dAmt, gstPercent)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun QuickCreateProductDialog(
    itemCode: String,
    onItemCodeChange: (String) -> Unit,
    itemName: String,
    onItemNameChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    sellingPrice: String,
    onSellingPriceChange: (String) -> Unit,
    gstPercent: Double,
    onGstPercentChange: (Double) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Quick Create Product",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = itemCode,
                    onValueChange = onItemCodeChange,
                    label = { Text("Item Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = itemName,
                    onValueChange = onItemNameChange,
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = onUnitChange,
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sellingPrice,
                        onValueChange = onSellingPriceChange,
                        label = { Text("Selling Price (₹)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GST Rate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.0, 5.0, 12.0, 18.0, 28.0).forEach { rateVal ->
                        val isSelected = gstPercent == rateVal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF3F4F6))
                                .clickable { onGstPercentChange(rateVal) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${rateVal.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF374151)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onSave) {
                        Text("Save Product")
                    }
                }
            }
        }
    }
}
