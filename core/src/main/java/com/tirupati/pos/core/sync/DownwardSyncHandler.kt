package com.tirupati.pos.core.sync

interface DownwardSyncHandler {
    suspend fun performDownwardSync()
}
