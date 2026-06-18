package com.tirupati.pos.core.sync

import kotlinx.coroutines.flow.StateFlow

/**
 * Orchestrates synchronization across repositories and remote.
 * Placeholder only — no business logic.
 */
interface SyncCoordinator {
    val state: StateFlow<SyncState>
    suspend fun startSync(): SyncResult
    fun stop()
}
