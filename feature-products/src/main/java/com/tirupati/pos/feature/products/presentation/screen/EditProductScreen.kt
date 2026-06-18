package com.tirupati.pos.feature.products.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.products.presentation.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    viewModel: ProductsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var product by remember { mutableStateOf<Product?>(null) }

    var selectedCompanyId by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var purchaseRate by remember { mutableStateOf("") }
    var sellingRate by remember { mutableStateOf("") }
    var stockQuantity by remember { mutableStateOf("") }

    var isError by remember { mutableStateOf(false) }
    var showCompanySheet by remember { mutableStateOf(false) }
    val companySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(productId) {
        val loaded = viewModel.getProduct(productId)
        if (loaded != null) {
            product = loaded
            selectedCompanyId = loaded.companyId
            itemName = loaded.itemName
            unit = loaded.unit
            purchaseRate = loaded.purchaseRate.toString()
            sellingRate = loaded.sellingRate.toString()
            stockQuantity = loaded.stockQuantity.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val selectedCompanyName = uiState.companies.find { it.id == selectedCompanyId }?.name ?: "Select Company"

                // Company selector — disabled field so clickable works; opens ModalBottomSheet
                OutlinedTextField(
                    value = selectedCompanyName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Company") },
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Company")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCompanySheet = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = product!!.itemCode,
                    onValueChange = {},
                    label = { Text("Item Code") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (e.g. Pcs, Kgs)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = purchaseRate,
                    onValueChange = { purchaseRate = it },
                    label = { Text("Purchase Rate") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = sellingRate,
                    onValueChange = { sellingRate = it },
                    label = { Text("Selling Rate") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = stockQuantity,
                    onValueChange = { stockQuantity = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isError) {
                    Text("Please fill all required fields correctly.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        val pRate = purchaseRate.toDoubleOrNull() ?: 0.0
                        val sRate = sellingRate.toDoubleOrNull() ?: 0.0
                        val stock = stockQuantity.toDoubleOrNull() ?: 0.0

                        if (selectedCompanyId.isBlank() || itemName.isBlank() || unit.isBlank()) {
                            isError = true
                        } else {
                            viewModel.editProduct(
                                id = productId,
                                companyId = selectedCompanyId,
                                itemName = itemName,
                                unit = unit,
                                purchaseRate = pRate,
                                sellingRate = sRate,
                                stockQuantity = stock
                            )
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Product")
                }
            }
        }
    }

    // ModalBottomSheet for company selection — outside Scaffold so it renders correctly
    if (showCompanySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCompanySheet = false },
            sheetState = companySheetState
        ) {
            LazyColumn {
                items(uiState.companies) { company ->
                    ListItem(
                        headlineContent = { Text(company.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCompanyId = company.id
                                showCompanySheet = false
                            }
                    )
                }
            }
        }
    }
}
