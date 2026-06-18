package com.tirupati.pos.domain.usecase

interface BaseUseCase<in P, R> {
    suspend operator fun invoke(params: P): R
}
