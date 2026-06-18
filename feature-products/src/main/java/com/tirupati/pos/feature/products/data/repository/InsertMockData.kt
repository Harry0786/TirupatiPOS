// SPDX-FileCopyrightText: 2024-2026 Jayes
// SPDX-License-Identifier: MIT

package com.tirupati.pos.feature.products.data.repository

import com.tirupati.pos.core.database.PendingOperation
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncQueue
import com.tirupati.pos.feature.products.domain.model.Company
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.products.domain.repository.CompanyRepository
import com.tirupati.pos.feature.products.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Utility class to insert mock data into the local database and queue sync operations.
 * This is intended for development and testing only.
 */
class InsertMockData @javax.inject.Inject constructor(
    private val companyRepository: CompanyRepository,
    private val productRepository: ProductRepository,
    private val syncQueue: SyncQueue,
    private val syncManager: SyncManager
) {
    /** Seed the database if it is empty. */
    fun seedIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if there are any companies already
            val existingCompanies = companyRepository.observeCompanies()
                .firstOrNull() ?: emptyList()
            if (existingCompanies.isNotEmpty()) return@launch

            // Insert 15 companies
            val companies = (1..15).map { index ->
                Company(
                    id = UUID.randomUUID().toString(),
                    name = "Company $index"
                )
            }
            companies.forEach { company ->
                // Save locally
                companyRepository.saveCompany(company)
                // Queue sync operation
                val op = PendingOperation(
                    id = UUID.randomUUID().toString(),
                    operationType = "INSERT",
                    entityType = "companies",
                    entityId = company.id,
                    payloadJson = Json.encodeToString(Company.serializer(), company),
                    timestamp = System.currentTimeMillis()
                )
                syncQueue.enqueue(op)
            }
            syncManager.requestSync()

            // Insert 100 products for each company
            companies.forEach { company ->
                (1..100).forEach { prodIndex ->
                    val product = Product(
                        id = UUID.randomUUID().toString(),
                        companyId = company.id,
                        itemCode = "${company.name.take(3).uppercase()}-${prodIndex}",
                        itemName = "Product ${prodIndex} of ${company.name}",
                        unit = "pcs",
                        purchaseRate = (10..100).random().toDouble(),
                        sellingRate = (110..200).random().toDouble(),
                        stockQuantity = (0..500).random().toDouble()
                    )
                    productRepository.saveProduct(product)
                    val op = PendingOperation(
                        id = UUID.randomUUID().toString(),
                        operationType = "INSERT",
                        entityType = "products",
                        entityId = product.id,
                        payloadJson = Json.encodeToString(Product.serializer(), product),
                        timestamp = System.currentTimeMillis()
                    )
                    syncQueue.enqueue(op)
                }
            }
            syncManager.requestSync()
        }
    }
}
