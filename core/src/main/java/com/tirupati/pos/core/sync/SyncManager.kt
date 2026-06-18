package com.tirupati.pos.core.sync

interface SyncManager {
    suspend fun requestSync()
}
