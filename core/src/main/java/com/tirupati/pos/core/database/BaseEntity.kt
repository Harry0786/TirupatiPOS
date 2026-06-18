package com.tirupati.pos.core.database

import java.io.Serializable

/**
 * BaseEntity provides common fields for all local entities.
 * Keep minimal to remain generic.
 */
interface BaseEntity : Serializable {
    val id: String
    val createdAt: Long
    val updatedAt: Long
}
