package com.tirupati.pos.feature.sales.data.repository

import com.tirupati.pos.core.database.PendingOperation
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncQueue
import com.tirupati.pos.feature.sales.data.local.EstimateDao
import com.tirupati.pos.feature.sales.data.local.InvoiceDao
import com.tirupati.pos.feature.sales.data.mapper.toDomain
import com.tirupati.pos.feature.sales.data.mapper.toLocal
import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.sales.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesRepositoryImpl @Inject constructor(
    private val estimateDao: EstimateDao,
    private val invoiceDao: InvoiceDao,
    private val productRepository: com.tirupati.pos.feature.products.domain.repository.ProductRepository,
    private val companyRepository: com.tirupati.pos.feature.products.domain.repository.CompanyRepository,
    private val syncQueue: SyncQueue,
    private val syncManager: SyncManager
) : SalesRepository {

    override fun observeEstimates(): Flow<List<Estimate>> =
        estimateDao.observeEstimates().map { list ->
            list.map { local ->
                val items = estimateDao.getItems(local.id)
                local.toDomain(items)
            }
        }

    override fun observeEstimate(id: String): Flow<Estimate?> =
        estimateDao.observeEstimate(id).map { local ->
            local?.let {
                val items = estimateDao.getItems(it.id)
                it.toDomain(items)
            }
        }

    override suspend fun getEstimate(id: String): Estimate? {
        val local = estimateDao.getEstimate(id) ?: return null
        val items = estimateDao.getItems(id)
        return local.toDomain(items)
    }

    override suspend fun saveEstimate(estimate: Estimate, items: List<EstimateItem>) {
        val localEst = estimate.toLocal()
        val localItems = items.map { it.toLocal().copy(estimateId = estimate.id) }
        estimateDao.saveEstimateWithItems(localEst, localItems)

        val updatedItems = items.map { it.copy(estimateId = estimate.id) }

        // Queue Sync Operation
        val op = PendingOperation(
            id = UUID.randomUUID().toString(),
            operationType = "INSERT",
            entityType = "estimates",
            entityId = estimate.id,
            payloadJson = Json.encodeToString(Estimate.serializer(), estimate.copy(items = updatedItems)),
            timestamp = System.currentTimeMillis()
        )
        syncQueue.enqueue(op)
        syncManager.requestSync()
    }

    override suspend fun updateEstimateStatus(id: String, status: String) {
        estimateDao.updateStatus(id, status)
        val estimate = getEstimate(id)
        if (estimate != null) {
            val op = PendingOperation(
                id = UUID.randomUUID().toString(),
                operationType = "UPDATE",
                entityType = "estimates",
                entityId = id,
                payloadJson = Json.encodeToString(Estimate.serializer(), estimate),
                timestamp = System.currentTimeMillis()
            )
            syncQueue.enqueue(op)
            syncManager.requestSync()
        }
    }

    override suspend fun getInvoice(id: String): Invoice? {
        val local = invoiceDao.getInvoice(id) ?: return null
        val items = invoiceDao.getItems(id)
        return local.toDomain(items)
    }

    override fun observeInvoice(id: String): Flow<Invoice?> =
        invoiceDao.observeInvoice(id).map { local ->
            local?.let {
                val items = invoiceDao.getItems(it.id)
                it.toDomain(items)
            }
        }

    override suspend fun saveInvoice(invoice: Invoice) {
        val localInv = invoice.toLocal()
        val localItems = invoice.items.map { it.toLocal(invoice.id) }
        invoiceDao.saveInvoiceWithItems(localInv, localItems)

        val updatedItems = invoice.items.map { it.copy(invoiceId = invoice.id) }
        val updatedInvoice = invoice.copy(items = updatedItems)

        // Queue Sync Operation
        val op = PendingOperation(
            id = UUID.randomUUID().toString(),
            operationType = "INSERT",
            entityType = "invoices",
            entityId = invoice.id,
            payloadJson = Json.encodeToString(Invoice.serializer(), updatedInvoice),
            timestamp = System.currentTimeMillis()
        )
        syncQueue.enqueue(op)
        syncManager.requestSync()
    }

    override suspend fun updateInvoicePayment(invoiceId: String, paymentMethod: String) {
        invoiceDao.updatePayment(invoiceId, paymentMethod, "PAID")
        val invoice = getInvoice(invoiceId)
        if (invoice != null) {
            val op = PendingOperation(
                id = UUID.randomUUID().toString(),
                operationType = "UPDATE",
                entityType = "invoices",
                entityId = invoiceId,
                payloadJson = Json.encodeToString(Invoice.serializer(), invoice),
                timestamp = System.currentTimeMillis()
            )
            syncQueue.enqueue(op)
            syncManager.requestSync()
        }
    }

    override fun observeProducts(): Flow<List<Product>> =
        productRepository.observeProducts()

    override suspend fun searchProducts(query: String): List<Product> =
        productRepository.searchProducts(query)

    override suspend fun saveProduct(product: Product) {
        productRepository.saveProduct(product)
    }

    override fun observeSalesForDate(date: String): Flow<Double> {
        return invoiceDao.observeSalesForDate(date).map { it ?: 0.0 }
    }

    override fun observeBillsCountForDate(date: String): Flow<Int> {
        return invoiceDao.observeBillsCountForDate(date).map { it ?: 0 }
    }

    override fun observeItemsSoldCountForDate(date: String): Flow<Int> {
        return invoiceDao.observeItemsSoldCountForDate(date).map { it ?: 0 }
    }

    override fun observeCustomersCountForDate(date: String): Flow<Int> {
        return invoiceDao.observeCustomersCountForDate(date).map { it ?: 0 }
    }

    override fun observeRecentInvoices(limit: Int): Flow<List<Invoice>> {
        return invoiceDao.observeRecentInvoices(limit).map { list -> list.map { it.toDomain(emptyList()) } }
    }
}
