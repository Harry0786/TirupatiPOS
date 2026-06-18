package com.tirupati.pos.feature.sales.domain.repository

import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.products.domain.model.Product
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

    fun observeSalesForDate(date: String): Flow<Double>
    fun observeBillsCountForDate(date: String): Flow<Int>
    fun observeItemsSoldCountForDate(date: String): Flow<Int>
    fun observeCustomersCountForDate(date: String): Flow<Int>
    fun observeRecentInvoices(limit: Int = 5): Flow<List<Invoice>>
}
