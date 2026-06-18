package com.tirupati.pos.core.sync

data class SyncUiState(
    val isNetworkAvailable: Boolean = false,
    val isBackendReachable: Boolean = false,
    val pendingOperationsCount: Int = 0,
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = 0L
)
