package com.tirupati.pos.feature.products.sync

import com.tirupati.pos.core.sync.DownwardSyncHandler
import com.tirupati.pos.feature.products.data.local.CompanyDao
import com.tirupati.pos.feature.products.data.local.ProductDao
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class SyncIdDto(val id: String)

@Serializable
data class CompanyDto(
    val id: String,
    val name: String
)

@Serializable
data class ProductDto(
    val id: String,
    @kotlinx.serialization.SerialName("company_id") val companyId: String,
    @kotlinx.serialization.SerialName("item_code") val itemCode: String,
    @kotlinx.serialization.SerialName("item_name") val itemName: String,
    val unit: String,
    @kotlinx.serialization.SerialName("purchase_rate") val purchaseRate: Double,
    @kotlinx.serialization.SerialName("selling_rate") val sellingRate: Double,
    @kotlinx.serialization.SerialName("stock_quantity") val stockQuantity: Double,
    @kotlinx.serialization.SerialName("created_at") val createdAt: Long = 0L,
    @kotlinx.serialization.SerialName("updated_at") val updatedAt: Long = 0L
)

@Singleton
class ProductsSyncHandler @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val companyDao: CompanyDao,
    private val productDao: ProductDao
) : DownwardSyncHandler {

    override suspend fun performDownwardSync() {
        syncCompanies()
        syncProducts()
    }

    private suspend fun syncCompanies() {
        val remoteIds = supabaseClient.postgrest["companies"]
            .select(columns = io.github.jan.supabase.postgrest.query.Columns.list("id"))
            .decodeList<SyncIdDto>().map { it.id }.toSet()

        val localIds = companyDao.getAllIds().toSet()

        val idsToDelete = localIds - remoteIds
        if (idsToDelete.isNotEmpty()) {
            companyDao.deleteByIds(idsToDelete.toList())
        }

        val idsToInsert = remoteIds - localIds
        if (idsToInsert.isNotEmpty()) {
            var offset = 0
            val pageSize = 1000
            val allRemoteCompanies = mutableListOf<com.tirupati.pos.feature.products.data.local.LocalCompany>()
            var allFetched = false
            while (!allFetched) {
                val page = supabaseClient.postgrest["companies"].select {
                    range((offset).toLong(), (offset + pageSize - 1).toLong())
                }.decodeList<CompanyDto>()
                
                val toInsert = page.filter { it.id in idsToInsert }.map {
                    com.tirupati.pos.feature.products.data.local.LocalCompany(id = it.id, name = it.name)
                }
                allRemoteCompanies.addAll(toInsert)
                if (page.size < pageSize) allFetched = true
                offset += pageSize
            }
            if (allRemoteCompanies.isNotEmpty()) {
                companyDao.insertCompanies(allRemoteCompanies)
            }
        }
    }

    private suspend fun syncProducts() {
        var offset = 0
        val pageSize = 1000
        var allFetched = false
        val remoteIds = mutableSetOf<String>()
        val allMissingProducts = mutableListOf<com.tirupati.pos.feature.products.data.local.LocalProduct>()
        
        val localIds = productDao.getAllIds().toSet()

        while (!allFetched) {
            val page = supabaseClient.postgrest["products"].select {
                range((offset).toLong(), (offset + pageSize - 1).toLong())
            }.decodeList<ProductDto>()
            
            remoteIds.addAll(page.map { it.id })
            
            val toInsert = page.filter { it.id !in localIds }.map {
                com.tirupati.pos.feature.products.data.local.LocalProduct(
                    id = it.id,
                    companyId = it.companyId,
                    itemCode = it.itemCode,
                    itemName = it.itemName,
                    unit = it.unit,
                    purchaseRate = it.purchaseRate,
                    sellingRate = it.sellingRate,
                    stockQuantity = it.stockQuantity,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
            allMissingProducts.addAll(toInsert)
            
            if (page.size < pageSize) allFetched = true
            offset += pageSize
        }

        val idsToDelete = localIds - remoteIds
        if (idsToDelete.isNotEmpty()) {
            productDao.deleteByIds(idsToDelete.toList())
        }
        if (allMissingProducts.isNotEmpty()) {
            productDao.insertProducts(allMissingProducts)
        }
    }
}
