package com.tirupati.pos.core.database

import androidx.room.RoomDatabase
import androidx.room.withTransaction

/**
 * Helper to execute database transactions in a clean manner.
 */
object DatabaseTransaction {
    suspend fun <R> run(db: RoomDatabase, block: suspend () -> R): R {
        return db.withTransaction(block)
    }
}
