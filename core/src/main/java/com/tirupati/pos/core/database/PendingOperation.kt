package com.tirupati.pos.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Generic pending operation to support offline-first queued changes.
 */
@Entity(tableName = "pending_operations")
data class PendingOperation(
    @PrimaryKey val id: String,
    val operationType: String,
    val entityType: String,
    val entityId: String,
    val payloadJson: String?,
    val timestamp: Long,
    val retryCount: Int = 0,
    val syncStatus: String = "PENDING"
)
