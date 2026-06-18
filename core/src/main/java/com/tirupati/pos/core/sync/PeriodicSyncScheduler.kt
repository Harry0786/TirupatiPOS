package com.tirupati.pos.core.sync

/**
 * Placeholder for WorkManager-based periodic sync scheduling.
 */
interface PeriodicSyncScheduler : SyncScheduler {
    fun scheduleWithIntervalMinutes(minutes: Long)
}

class NoOpPeriodicSyncScheduler : PeriodicSyncScheduler {
    override fun schedulePeriodicSync() = Unit
    override fun scheduleWithIntervalMinutes(minutes: Long) = Unit
}
