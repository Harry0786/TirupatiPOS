package com.tirupati.pos.domain.repository

sealed interface RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>
    data class Error(val throwable: Throwable) : RepositoryResult<Nothing>
    object Loading : RepositoryResult<Nothing>
    object Offline : RepositoryResult<Nothing>
}
