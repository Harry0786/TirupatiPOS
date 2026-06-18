package com.tirupati.pos.core.network

interface NetworkMonitor {
    suspend fun hasNetworkConnection(): Boolean
}
