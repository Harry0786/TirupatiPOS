package com.tirupati.pos.feature.products.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Company(
    val id: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
