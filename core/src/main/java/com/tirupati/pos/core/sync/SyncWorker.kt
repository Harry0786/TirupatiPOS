package com.tirupati.pos.core.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tirupati.pos.core.network.NetworkMonitor
import androidx.hilt.work.HiltWorker
import dagger.assisted.AssistedInject
import dagger.assisted.Assisted

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val syncQueue: SyncQueue,
    private val supabaseClient: SupabaseClient,
    private val networkMonitor: NetworkMonitor,
    private val syncStatusManager: SyncStatusManager
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        // network status check
        android.util.Log.d("SyncWorker", "Network available: ${networkMonitor.hasNetworkConnection()}")
        if (!networkMonitor.hasNetworkConnection()) {
            return Result.retry()
        }



        var hasFailure = false
        val batchSize = 50
        var pendingOps = syncQueue.getBatch(batchSize)
        android.util.Log.d("SyncWorker", "Initial pending ops batch size: ${pendingOps.size}")

        while (pendingOps.isNotEmpty() && !hasFailure) {
            for (op in pendingOps) {
                try {
                    android.util.Log.d(
                        "SyncWorker",
                        "Processing op id=${op.id} type=${op.operationType} entity=${op.entityType}"
                    )
                    // Table name convention: lower-cased plural/snake_case representation
                    val tableName = op.entityType.lowercase().trim()

                    when (op.operationType.uppercase()) {
                        "INSERT", "UPDATE" -> {
                            if (op.payloadJson != null) {
                                val element = Json.parseToJsonElement(op.payloadJson)
                                if (element is JsonObject) {
                                    val jsonObject = element.jsonObject
                                    val itemsArray = jsonObject["items"]?.jsonArray
                                    
                                    val map = mutableMapOf<String, JsonElement>()
                                    jsonObject.entries.forEach { (key, value) ->
                                        if (key != "items") {
                                            val snakeKey = key.replace(Regex("([a-z])([A-Z]+)")) {
                                                "${it.groupValues[1]}_${it.groupValues[2].lowercase()}"
                                            }
                                            map[snakeKey] = value
                                        }
                                    }
                                    val snakeObject = JsonObject(map)
                                    
                                    supabaseClient.postgrest[tableName].upsert(snakeObject)
                                    
                                    if (itemsArray != null && itemsArray.isNotEmpty()) {
                                        val itemTableName = when (tableName) {
                                            "estimates" -> "estimate_items"
                                            "invoices" -> "invoice_items"
                                            else -> null
                                        }
                                        if (itemTableName != null) {
                                            val snakeItems = itemsArray.map { item ->
                                                val itemMap = mutableMapOf<String, JsonElement>()
                                                item.jsonObject.entries.forEach { (key, value) ->
                                                    val snakeKey = key.replace(Regex("([a-z])([A-Z]+)")) {
                                                        "${it.groupValues[1]}_${it.groupValues[2].lowercase()}"
                                                    }
                                                    itemMap[snakeKey] = value
                                                }
                                                JsonObject(itemMap)
                                            }
                                            supabaseClient.postgrest[itemTableName].upsert(snakeItems)
                                        }
                                    }
                                }
                            }
                        }
                        "DELETE" -> {
                            supabaseClient.postgrest[tableName].delete {
                                filter { eq("id", op.entityId) }
                            }
                        }
                    }
                    // Successfully synchronized, dequeue the operation
                    syncQueue.dequeue(op.id)
                    android.util.Log.d("SyncWorker", "Successfully synced op id=${op.id}")
                } catch (t: Throwable) {
                    android.util.Log.e("SyncWorker", "Error syncing op id=${op.id}", t)
                    hasFailure = true
                    break
                }
            }

            if (!hasFailure) {
                pendingOps = syncQueue.getBatch(batchSize)
            }
        }

        if (!hasFailure) {
            syncStatusManager.updateLastSyncTime()
        }
        return if (hasFailure) Result.retry() else Result.success()
    }

}
