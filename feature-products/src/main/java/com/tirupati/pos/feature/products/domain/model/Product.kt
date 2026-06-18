package com.tirupati.pos.feature.products.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val companyId: String,
    val itemCode: String,
    val itemName: String,
    val unit: String,
    val purchaseRate: Double,
    val sellingRate: Double,
    val stockQuantity: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
