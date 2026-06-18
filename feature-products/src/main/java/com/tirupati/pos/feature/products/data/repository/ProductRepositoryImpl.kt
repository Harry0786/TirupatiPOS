package com.tirupati.pos.feature.products.data.repository

import com.tirupati.pos.core.database.PendingOperation
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncQueue
import com.tirupati.pos.feature.products.data.local.ProductDao
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.products.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val syncQueue: SyncQueue,
    private val syncManager: SyncManager
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeProducts().map { list ->
            list.map { it.toDomain() }
        }

    override fun observeProductsByCompany(companyId: String): Flow<List<Product>> =
        productDao.observeProductsByCompany(companyId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getProduct(id: String): Product? {
        return productDao.getProduct(id)?.toDomain()
    }

    override suspend fun searchProducts(query: String): List<Product> {
        return productDao.searchProducts(query).map { it.toDomain() }
    }

    override suspend fun saveProduct(product: Product) {
        val local = product.toLocal()
        productDao.insert(local)

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
