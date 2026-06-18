package com.tirupati.pos.core.sync

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpSyncScheduler @Inject constructor() : SyncScheduler {
    override fun schedulePeriodicSync() = Unit
}
