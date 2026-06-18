package com.tirupati.pos.feature.products.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.products.presentation.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    viewModel: ProductsViewModel,
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var companyName by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        val loaded = viewModel.getProduct(productId)
        if (loaded != null) {
            product = loaded
            companyName = uiState.companies.find { it.id == loaded.companyId }?.name ?: "Unknown"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            ProductDetailContent(
                product = product!!,
                companyName = companyName,
                onEditClick = onEditClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Product,
    companyName: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = product.itemName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        DetailRow("Company", companyName)
        DetailRow("Item Code", product.itemCode)
        DetailRow("Unit", product.unit)
        DetailRow("Purchase Rate", "₹${product.purchaseRate}")
        DetailRow("Selling Rate", "₹${product.sellingRate}")
        DetailRow("Current Stock", "${product.stockQuantity} ${product.unit}")
        // Last Updated timestamp formatted
        val formattedUpdated = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(product.updatedAt))
        DetailRow("Last Updated", formattedUpdated)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onEditClick, modifier = Modifier.fillMaxWidth()) {
            Text("Edit Details")
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
