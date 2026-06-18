package com.tirupati.pos.core.sync

class DefaultSyncManager : SyncManager {
    override suspend fun requestSync() = Unit
}
