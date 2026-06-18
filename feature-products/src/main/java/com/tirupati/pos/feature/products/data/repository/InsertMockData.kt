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
import kotlin.random.Random

/**
 * Utility class to insert mock data into the local database and queue sync operations.
 * This matches the python script mock data schema.
 */
class InsertMockData @javax.inject.Inject constructor(
    private val companyRepository: CompanyRepository,
    private val productRepository: ProductRepository,
    private val syncQueue: SyncQueue,
    private val syncManager: SyncManager
) {
    private val companiesList = listOf(
        "Havells", "Polycab", "Finolex", "Legrand", "Anchor", 
        "Schneider", "Syska", "Philips", "Bajaj", "Crompton", 
        "V-Guard", "Orient", "Usha", "Wipro", "L&T"
    )

    private data class Category(
        val name: String,
        val unit: String,
        val minPrice: Int,
        val maxPrice: Int
    )

    private val categories = listOf(
        Category("Wire 1.0 sq mm", "Coil", 800, 1050),
        Category("Wire 1.5 sq mm", "Coil", 1200, 1500),
        Category("Wire 2.5 sq mm", "Coil", 1800, 2200),
        Category("Wire 4.0 sq mm", "Coil", 2500, 3100),
        Category("Switch 6A", "Pcs", 25, 45),
        Category("Switch 16A", "Pcs", 45, 75),
        Category("Socket 6A", "Pcs", 30, 55),
        Category("Socket 16A", "Pcs", 60, 95),
        Category("MCB 10A SP", "Pcs", 120, 180),
        Category("MCB 16A SP", "Pcs", 120, 180),
        Category("MCB 32A DP", "Pcs", 350, 480),
        Category("MCB 63A DP", "Pcs", 450, 600),
        Category("LED Bulb 9W", "Pcs", 65, 110),
        Category("LED Bulb 12W", "Pcs", 90, 150),
        Category("Ceiling Fan 1200mm", "Pcs", 1400, 1900),
        Category("Exhaust Fan 150mm", "Pcs", 800, 1100),
        Category("PVC Pipe 25mm", "Bundle", 300, 450),
        Category("PVC Pipe 20mm", "Bundle", 250, 380),
        Category("Casing Capping 1 inch", "Bundle", 150, 250),
        Category("Distribution Board 4 Way", "Pcs", 400, 650)
    )

    private val colorsOrTypes = listOf(
        "Red", "Black", "Blue", "Green", "White", 
        "Standard", "Premium", "Heavy Duty", "Gold", "Silver"
    )

    /** Seed the database if it is empty. */
    fun seedIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if there are any companies already
            val existingCompanies = companyRepository.observeCompanies()
                .firstOrNull() ?: emptyList()
            if (existingCompanies.isNotEmpty()) return@launch

            val seededCompanies = companiesList.map { name ->
                Company(
                    id = UUID.randomUUID().toString(),
                    name = name
                )
            }

            seededCompanies.forEach { company ->
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

            seededCompanies.forEach { company ->
                var productsWritten = 0
                val random = Random(System.currentTimeMillis())
                
                for (cat in categories) {
                    for (variant in colorsOrTypes) {
                        if (productsWritten >= 100) break

                        val pId = UUID.randomUUID().toString()
                        val itemName = "${company.name} ${cat.name} $variant"
                        val itemCode = "${company.name.take(3).uppercase()}-${cat.name.take(3).replace(" ", "").uppercase()}-${variant.take(3).uppercase()}-${String.format("%03d", productsWritten + 1)}"

                        val pRate = random.nextInt(cat.minPrice, cat.maxPrice + 1)
                        val sRate = (pRate * random.nextDouble(1.15, 1.40)).toInt()
                        val stock = random.nextInt(10, 501)

                        val product = Product(
                            id = pId,
                            companyId = company.id,
                            itemCode = itemCode,
                            itemName = itemName,
                            unit = cat.unit,
                            purchaseRate = pRate.toDouble(),
                            sellingRate = sRate.toDouble(),
                            stockQuantity = stock.toDouble()
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
                        productsWritten++
                    }
                    if (productsWritten >= 100) break
                }
            }
            syncManager.requestSync()
        }
    }
}
