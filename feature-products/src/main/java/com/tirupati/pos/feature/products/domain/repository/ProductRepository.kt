package com.tirupati.pos.feature.products.domain.repository

import com.tirupati.pos.feature.products.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    fun observeProductsByCompany(companyId: String): Flow<List<Product>>
    suspend fun getProduct(id: String): Product?
    suspend fun searchProducts(query: String): List<Product>
    suspend fun saveProduct(product: Product)
}
