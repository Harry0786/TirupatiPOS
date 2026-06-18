package com.tirupati.pos.feature.sales.sync

import com.tirupati.pos.core.sync.DownwardSyncHandler
import com.tirupati.pos.feature.sales.data.local.EstimateDao
import com.tirupati.pos.feature.sales.data.local.InvoiceDao
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class SyncIdDto(val id: String)

@Singleton
class SalesSyncHandler @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val estimateDao: EstimateDao,
    private val invoiceDao: InvoiceDao
) : DownwardSyncHandler {

    override suspend fun performDownwardSync() {
        syncTable("estimates", estimateDao)
        syncTable("invoices", invoiceDao)
    }

    private suspend fun syncTable(tableName: String, dao: Any) {
        // Fetch all IDs from remote
        val remoteIds = supabaseClient.postgrest[tableName].select(columns = io.github.jan.supabase.postgrest.query.Columns.list("id")) {
        }.decodeList<SyncIdDto>().map { it.id }.toSet()

        // We fetch all local IDs. The DAOs don't have a specific getIds method, so we fetch the objects.
        // Wait, fetching all local objects might be heavy if there are thousands. Let's add a getIds query to the DAOs.
        val localIds = when (dao) {
            is EstimateDao -> dao.getAllIds()
            is InvoiceDao -> dao.getAllIds()
            else -> emptyList()
        }.toSet()

        val idsToDelete = localIds - remoteIds

        if (idsToDelete.isNotEmpty()) {
            when (dao) {
                is EstimateDao -> dao.deleteByIds(idsToDelete.toList())
                is InvoiceDao -> dao.deleteByIds(idsToDelete.toList())
            }
        }
    }
}
