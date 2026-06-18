package com.tirupati.pos.feature.sales.data.repository

import com.tirupati.pos.core.database.PendingOperation
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncQueue
import com.tirupati.pos.feature.sales.data.local.EstimateDao
import com.tirupati.pos.feature.sales.data.local.InvoiceDao
import com.tirupati.pos.feature.sales.data.local.ProductDao
import com.tirupati.pos.feature.sales.data.mapper.toDomain
import com.tirupati.pos.feature.sales.data.mapper.toLocal
import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.sales.domain.model.Product
import com.tirupati.pos.feature.sales.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesRepositoryImpl @Inject constructor(
    private val estimateDao: EstimateDao,
    private val invoiceDao: InvoiceDao,
    private val productDao: ProductDao,
    private val syncQueue: SyncQueue,
    private val syncManager: SyncManager
) : SalesRepository {

    init {
        // Run seed in a background scope or simply block temporarily since it's local
        // To be safe, we will seed on demand or inside initialize
    }

    suspend fun seedPlaceholderProductsIfEmpty() {
        if (productDao.getProductCount() == 0) {
            val placeholders = listOf(
                Product(UUID.randomUUID().toString(), "LB12", "LED Bulb", "Pcs", 120.0, 12.0),
                Product(UUID.randomUUID().toString(), "WR01", "Copper Wire", "Roll", 850.0, 18.0),
                Product(UUID.randomUUID().toString(), "SW02", "Modular Switch 6A", "Pcs", 45.0, 18.0),
                Product(UUID.randomUUID().toString(), "PL03", "3 Pin Plug 16A", "Pcs", 75.0, 18.0),
                Product(UUID.randomUUID().toString(), "FN04", "Ceiling Fan 48\"", "Pcs", 2400.0, 28.0)
            )
            productDao.insertProducts(placeholders.map { it.toLocal() })
        }
    }

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

        // Queue Sync Operation
        val op = PendingOperation(
            id = UUID.randomUUID().toString(),
            operationType = "INSERT",
            entityType = "estimates",
            entityId = estimate.id,
            payloadJson = Json.encodeToString(Estimate.serializer(), estimate.copy(items = items)),
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

        // Queue Sync Operation
        val op = PendingOperation(
            id = UUID.randomUUID().toString(),
            operationType = "INSERT",
            entityType = "invoices",
            entityId = invoice.id,
            payloadJson = Json.encodeToString(Invoice.serializer(), invoice),
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
        productDao.observeProducts().map { list -> list.map { it.toDomain() } }

    override suspend fun searchProducts(query: String): List<Product> {
        seedPlaceholderProductsIfEmpty()
        return productDao.searchProducts(query).map { it.toDomain() }
    }

    override suspend fun saveProduct(product: Product) {
        productDao.insert(product.toLocal())

        // Queue Sync Operation
        val op = PendingOperation(
            id = UUID.randomUUID().toString(),
            operationType = "INSERT",
            entityType = "products",
            entityId = product.id,
            payloadJson = Json.encodeToString(Product.serializer(), product),
            timestamp = System.currentTimeMillis()
        )
        syncQueue.enqueue(op)
        syncManager.requestSync()
    }
}
