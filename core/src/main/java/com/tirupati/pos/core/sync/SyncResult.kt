package com.tirupati.pos.core.sync

sealed interface SyncResult {
    object Success : SyncResult
    data class Partial(val pending: Int) : SyncResult
    data class Failure(val error: Throwable) : SyncResult
}
