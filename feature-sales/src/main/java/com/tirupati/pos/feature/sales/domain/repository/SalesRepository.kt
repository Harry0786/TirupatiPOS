package com.tirupati.pos.feature.sales.domain.repository

import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.sales.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface SalesRepository {
    fun observeEstimates(): Flow<List<Estimate>>
    fun observeEstimate(id: String): Flow<Estimate?>
    suspend fun getEstimate(id: String): Estimate?
    suspend fun saveEstimate(estimate: Estimate, items: List<EstimateItem>)
    suspend fun updateEstimateStatus(id: String, status: String)
    suspend fun getInvoice(id: String): Invoice?
    fun observeInvoice(id: String): Flow<Invoice?>
    suspend fun saveInvoice(invoice: Invoice)
    suspend fun updateInvoicePayment(invoiceId: String, paymentMethod: String)
    fun observeProducts(): Flow<List<Product>>
    suspend fun searchProducts(query: String): List<Product>
    suspend fun saveProduct(product: Product)
}
