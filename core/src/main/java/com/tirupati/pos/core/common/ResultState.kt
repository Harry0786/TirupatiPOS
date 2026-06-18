package com.tirupati.pos.core.common

sealed interface ResultState<out T> {
    object Loading : ResultState<Nothing>
    data class Success<T>(val data: T) : ResultState<T>
    data class Error(val throwable: Throwable) : ResultState<Nothing>
    object Empty : ResultState<Nothing>
    object Offline : ResultState<Nothing>
    object Syncing : ResultState<Nothing>
    data class Conflict(val details: String?) : ResultState<Nothing>
}
