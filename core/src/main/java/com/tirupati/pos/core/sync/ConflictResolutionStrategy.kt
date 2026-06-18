package com.tirupati.pos.core.sync

/**
 * Strategy interface to resolve conflicts when synchronizing.
 * Implementations should be business-aware; provide placeholders here.
 */
fun interface ConflictResolutionStrategy {
    fun resolve(remoteJson: String?, localJson: String?): String?
}
