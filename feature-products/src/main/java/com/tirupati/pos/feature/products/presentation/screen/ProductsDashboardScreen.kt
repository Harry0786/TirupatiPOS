package com.tirupati.pos.feature.products.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList

import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
// Duplicate import removed
import com.tirupati.pos.core.ui.layout.WindowWidthSizeClass
import com.tirupati.pos.core.ui.layout.calculateWindowWidthSizeClass
import com.tirupati.pos.feature.products.domain.model.Company
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.products.presentation.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsDashboardScreen(
    viewModel: ProductsViewModel,
    onAddCompanyClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onEditProductClick: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCompanySheet by remember { mutableStateOf(false) }
    val widthClass = calculateWindowWidthSizeClass()
    val isTablet = widthClass != WindowWidthSizeClass.Compact

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onAddCompanyClick) {
                        Text("Add Company")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onAddProductClick) {
                        Text("Add Product")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (isTablet) {
            // Master-Detail tablet responsive layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Master List Panel (40% width)
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search Product...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        IconButton(onClick = { showCompanySheet = true }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProductsList(
                        products = uiState.products,
                        companies = uiState.companies,
                        selectedId = uiState.selectedProductId,
                        onItemClick = { viewModel.selectProduct(it.id) }
                    )
                }

                                if (showCompanySheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showCompanySheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ) {
                        // Clear filter option
                        ListItem(
                            headlineContent = { Text("All Companies") },
                            leadingContent = { Icon(Icons.Filled.FilterList, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCompany(null)
                                    showCompanySheet = false
                                }
                        )
                        LazyColumn {
                            items(uiState.companies) { company ->
                                ListItem(
                                    headlineContent = { Text(company.name) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectCompany(company.id)
                                            showCompanySheet = false
                                        }
                                )
                            }
                        }
                    }
                }
                // Detail Panel (60% width)
                Box(
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                ) {
                    val selectedProduct = uiState.products.find { it.id == uiState.selectedProductId }
                    if (selectedProduct != null) {
                        val companyName = uiState.companies.find { it.id == selectedProduct.companyId }?.name ?: "Unknown"
                        ProductDetailContent(
                            product = selectedProduct,
                            companyName = companyName,
                            onEditClick = { onEditProductClick(selectedProduct.id) }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Select a Product",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9CA3AF)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Choose a product from the list to view details",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Single Column Phone list layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search Product...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    IconButton(onClick = { showCompanySheet = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                ProductsList(
                    products = uiState.products,
                    companies = uiState.companies,
                    selectedId = null,
                    onItemClick = { onProductClick(it.id) }
                )
            }
        }
    }
}



@Composable
fun ProductsList(
    products: List<Product>,
    companies: List<Company>,
    selectedId: String?,
    onItemClick: (Product) -> Unit
) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Products Found",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7280)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(products) { product ->
                val companyName = companies.find { it.id == product.companyId }?.name ?: "Unknown"
                ProductListItem(
                    product = product,
                    companyName = companyName,
                    isSelected = product.id == selectedId,
                    onClick = { onItemClick(product) }
                )
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: Product,
    companyName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = companyName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1)
        )
        Text(
            text = product.itemName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
}
