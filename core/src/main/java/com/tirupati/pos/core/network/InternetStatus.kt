package com.tirupati.pos.core.network

sealed interface InternetStatus {
    object Available : InternetStatus
    object Unavailable : InternetStatus
    data class Limited(val reason: String?) : InternetStatus
}
