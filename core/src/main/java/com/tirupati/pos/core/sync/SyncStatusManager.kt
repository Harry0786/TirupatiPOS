package com.tirupati.pos.core.sync

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.tirupati.pos.core.network.ConnectivityObserver
import com.tirupati.pos.core.network.SupabaseHealthService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.channels.BufferOverflow

@Singleton
class SyncStatusManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val connectivityObserver: ConnectivityObserver,
    private val healthService: SupabaseHealthService,
    private val syncQueue: SyncQueue
) {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val healthCheckTrigger = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        tryEmit(Unit)
    }

    // A flow that periodically checks backend health and can be triggered manually
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val backendHealthFlow = healthCheckTrigger.flatMapLatest {
        flow {
            while (true) {
                val isHealthy = healthService.checkHealth()
                emit(isHealthy)
                delay(30000) // check every 30 seconds
            }
        }
    }

    fun refreshBackendHealth() {
        healthCheckTrigger.tryEmit(Unit)
    }

    private val workManager = WorkManager.getInstance(context)
    private val workerFlow = workManager.getWorkInfosByTagFlow(SyncWorker::class.java.name)
        .map { workInfos ->
            workInfos.any { it.state == WorkInfo.State.RUNNING }
        }

    // Keep track of the last sync time. In a real app this might be saved in DataStore.
    private val lastSyncTimeFlow = MutableStateFlow(System.currentTimeMillis())

    val syncUiState: StateFlow<SyncUiState> = combine(
        connectivityObserver.isConnected,
        backendHealthFlow,
        syncQueue.observePending().map { it.size },
        workerFlow,
        lastSyncTimeFlow
    ) { isConnected, isBackendReachable, pendingCount, isSyncing, lastSyncTime ->
        SyncUiState(
            isNetworkAvailable = isConnected,
            isBackendReachable = isBackendReachable,
            pendingOperationsCount = pendingCount,
            isSyncing = isSyncing,
            lastSyncTime = lastSyncTime
        )
    }.stateIn(
        scope = applicationScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SyncUiState()
    )

    fun updateLastSyncTime(time: Long = System.currentTimeMillis()) {
        lastSyncTimeFlow.update { time }
    }
}
