package com.tirupati.pos.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Generic local data source backed by Room. Feature modules implement concrete types.
 */
interface LocalDataSource<E, ID> {
    fun observeById(id: ID): Flow<E?>
    suspend fun getById(id: ID): E?
    suspend fun insert(entity: E)
    suspend fun update(entity: E)
    suspend fun delete(entity: E)
}
