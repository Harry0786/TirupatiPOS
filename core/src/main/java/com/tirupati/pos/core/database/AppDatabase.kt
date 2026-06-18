package com.tirupati.pos.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
	entities = [PendingOperation::class],
	version = 1,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun pendingOperationDao(): PendingOperationDao
}
