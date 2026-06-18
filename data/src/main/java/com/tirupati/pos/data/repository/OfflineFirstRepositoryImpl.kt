package com.tirupati.pos.data.repository

import com.tirupati.pos.core.database.PendingOperation
import com.tirupati.pos.core.sync.SyncQueue
import com.tirupati.pos.domain.repository.OfflineFirstRepository
import kotlinx.coroutines.flow.Flow

/**
 * Generic offline-first repository base implementation.
 * Concrete repositories in feature modules will extend this.
 */
abstract class OfflineFirstRepositoryImpl<T, ID>(
    private val syncQueue: SyncQueue
) : OfflineFirstRepository<T, ID> {

    override suspend fun enqueueOperation(op: Any) {
        // Convert to PendingOperation and enqueue — placeholder only
        if (op is PendingOperation) syncQueue.enqueue(op)
    }

    abstract override fun observeLocal(id: ID): Flow<T?>

    abstract override suspend fun getLocal(id: ID): T?
}
