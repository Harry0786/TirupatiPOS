package com.tirupati.pos.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Base contract for offline-first repositories.
 * Implementations must expose local Room data as Flow.
 */
interface OfflineFirstRepository<T, ID> {
    fun observeLocal(id: ID): Flow<T?>
    suspend fun getLocal(id: ID): T?
    suspend fun enqueueOperation(op: Any)
}
