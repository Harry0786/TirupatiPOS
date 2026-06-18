package com.tirupati.pos.core.network

data class NetworkState(
    val isConnected: Boolean,
    val status: InternetStatus
)
