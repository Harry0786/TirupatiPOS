package com.tirupati.pos.feature.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirupati.pos.feature.products.domain.model.Company
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.products.domain.repository.CompanyRepository
import com.tirupati.pos.feature.products.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ProductsUiState(
    val companies: List<Company> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCompanyId: String? = null,
    val selectedProductId: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val companyRepository: CompanyRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCompanyId = MutableStateFlow<String?>(null)

    private val _selectedProductId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProductsUiState> = combine(
        companyRepository.observeCompanies(),
        _selectedCompanyId.flatMapLatest { companyId ->
            if (companyId == null) {
                productRepository.observeProducts()
            } else {
                productRepository.observeProductsByCompany(companyId)
            }
        },
        _selectedCompanyId,
        _selectedProductId,
        _searchQuery
    ) { companies, products, selectedCompanyId, selectedProductId, searchQuery ->
        val filteredProducts = if (searchQuery.isBlank()) {
            products
        } else {
            products.filter {
                it.itemName.contains(searchQuery, ignoreCase = true) ||
                it.itemCode.contains(searchQuery, ignoreCase = true) ||
                (companies.find { c -> c.id == it.companyId }?.name?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
        ProductsUiState(
            companies = companies,
            products = filteredProducts,
            selectedCompanyId = selectedCompanyId,
            selectedProductId = selectedProductId,
            searchQuery = searchQuery,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductsUiState(isLoading = true)
    )

    fun selectCompany(companyId: String?) {
        _selectedCompanyId.value = companyId
    }

    fun selectProduct(productId: String?) {
        _selectedProductId.value = productId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCompany(name: String) {
        viewModelScope.launch {
            val newCompany = Company(
                id = UUID.randomUUID().toString(),
                name = name
            )
            companyRepository.saveCompany(newCompany)
        }
    }

    fun addProduct(
        companyId: String,
        itemCode: String,
        itemName: String,
        unit: String,
        purchaseRate: Double,
        sellingRate: Double,
        stockQuantity: Double
    ) {
        viewModelScope.launch {
            val newProduct = Product(
                id = UUID.randomUUID().toString(),
                companyId = companyId,
                itemCode = itemCode,
                itemName = itemName,
                unit = unit,
                purchaseRate = purchaseRate,
                sellingRate = sellingRate,
                stockQuantity = stockQuantity
            )
            productRepository.saveProduct(newProduct)
        }
    }

    fun editProduct(
        id: String,
        companyId: String,
        itemName: String,
        unit: String,
        purchaseRate: Double,
        sellingRate: Double,
        stockQuantity: Double
    ) {
        viewModelScope.launch {
            val existing = productRepository.getProduct(id)
            if (existing != null) {
                val updated = existing.copy(
                    companyId = companyId,
                    itemName = itemName,
                    unit = unit,
                    purchaseRate = purchaseRate,
                    sellingRate = sellingRate,
                    stockQuantity = stockQuantity,
                    updatedAt = System.currentTimeMillis()
                )
                productRepository.saveProduct(updated)
            }
        }
    }



    // Helper to get a single product for details/edit screens
    suspend fun getProduct(id: String): Product? {
        return productRepository.getProduct(id)
    }
}
