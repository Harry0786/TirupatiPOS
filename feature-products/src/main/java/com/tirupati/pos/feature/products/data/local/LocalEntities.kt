package com.tirupati.pos.feature.products.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.tirupati.pos.core.database.BaseEntity

@Entity(
    tableName = "companies",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class LocalCompany(
    @PrimaryKey override val id: String,
    val name: String,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis()
) : BaseEntity

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["itemCode"], unique = true),
        Index(value = ["itemName"]),
        Index(value = ["companyId"])
    ]
)
data class LocalProduct(
    @PrimaryKey override val id: String,
    val companyId: String,
    val itemCode: String,
    val itemName: String,
    val unit: String,
    val purchaseRate: Double,
    val sellingRate: Double,
    val stockQuantity: Double,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis()
) : BaseEntity
