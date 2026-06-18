package com.tirupati.pos.core.network

class NoOpNetworkMonitor : NetworkMonitor {
    override suspend fun hasNetworkConnection(): Boolean = false
}
