package com.tirupati.pos.core.sync

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpSyncManager @Inject constructor() : SyncManager {
    override suspend fun requestSync() {
        // Do nothing
    }

    override fun startPeriodicSync() {
        // Do nothing
    }
}
