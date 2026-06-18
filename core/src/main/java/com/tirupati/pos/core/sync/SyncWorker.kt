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

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val syncQueue: SyncQueue,
    private val supabaseClient: SupabaseClient,
    private val networkMonitor: NetworkMonitor
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        // network status check
        android.util.Log.d("SyncWorker", "Network available: ${networkMonitor.hasNetworkConnection()}")
        if (!networkMonitor.hasNetworkConnection()) {
            return Result.retry()
        }



        val pendingOps = syncQueue.drain()
        android.util.Log.d("SyncWorker", "Pending ops count: ${pendingOps.size}")
        if (pendingOps.isEmpty()) {
            return Result.success()
        }

        var hasFailure = false

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
                                supabaseClient.postgrest[tableName].upsert(element)
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
            }
        }

        return if (hasFailure) Result.retry() else Result.success()
    }

}
