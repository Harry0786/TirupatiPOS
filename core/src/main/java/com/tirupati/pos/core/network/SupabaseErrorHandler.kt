package com.tirupati.pos.core.network

import java.io.IOException

sealed interface SupabaseError {
    data class Network(val cause: IOException) : SupabaseError
    data class Server(val message: String) : SupabaseError
    data class Unknown(val throwable: Throwable) : SupabaseError
}

object SupabaseErrorHandler {
    fun map(e: Throwable): SupabaseError = when (e) {
        is IOException -> SupabaseError.Network(e)
        else -> SupabaseError.Unknown(e)
    }
}
