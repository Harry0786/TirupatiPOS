package com.tirupati.pos.core.sync

import com.tirupati.pos.core.database.PendingOperation
import com.tirupati.pos.core.database.PendingOperationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SyncQueue @Inject constructor(
    private val dao: PendingOperationDao
) {
    fun observePending(): Flow<List<PendingOperation>> = dao.observeAll()

    suspend fun enqueue(op: PendingOperation) {
        dao.insert(op)
    }

    suspend fun dequeue(id: String) {
        dao.removeById(id)
    }

    suspend fun drain(): List<PendingOperation> = dao.getAll()
}
