package com.tirupati.pos.core.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingOperationDao : BaseDao<PendingOperation> {

    @Query("SELECT * FROM pending_operations ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<PendingOperation>>

    @Query("SELECT * FROM pending_operations ORDER BY timestamp ASC")
    suspend fun getAll(): List<PendingOperation>

    @Query("DELETE FROM pending_operations WHERE id = :id")
    suspend fun removeById(id: String)
}
