package com.tirupati.pos.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class NoOpConnectivityObserver : ConnectivityObserver {
    override val isConnected: Flow<Boolean> = MutableStateFlow(false)
}
