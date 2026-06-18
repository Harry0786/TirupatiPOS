package com.tirupati.pos.core.sync

sealed interface SyncState {
    object Idle : SyncState
    object Running : SyncState
    data class Completed(val result: SyncResult) : SyncState
    data class Failed(val error: Throwable) : SyncState
}
