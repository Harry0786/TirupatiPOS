package com.tirupati.pos.core.util.errors

sealed interface AppError {
    data class NetworkError(val message: String? = null) : AppError
    data class DatabaseError(val message: String? = null) : AppError
    data class SyncError(val message: String? = null) : AppError
    data class SerializationError(val message: String? = null) : AppError
    data class UnknownError(val throwable: Throwable) : AppError
}
