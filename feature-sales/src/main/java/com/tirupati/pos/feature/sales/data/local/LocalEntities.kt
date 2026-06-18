package com.tirupati.pos.feature.sales.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.tirupati.pos.core.database.BaseEntity

@Entity(
    tableName = "estimates",
    indices = [
        Index(value = ["estimateNumber"], unique = true),
        Index(value = ["customerName"]),
        Index(value = ["customerPhone"]),
        Index(value = ["status"])
    ]
)
data class LocalEstimate(
    @PrimaryKey override val id: String,
    val estimateNumber: String,
    val customerName: String,
    val customerPhone: String = "",
    val customerAddress: String = "",
    val date: String,
    val time: String,
    val status: String,
    val subtotal: Double,
    val discountTotal: Double,
    val gstTotal: Double,
    val grandTotal: Double,
    override val createdAt: Long,
    override val updatedAt: Long
) : BaseEntity

@Entity(
    tableName = "estimate_items",
    indices = [
        Index(value = ["estimateId"]),
        Index(value = ["productId"])
    ]
)
data class LocalEstimateItem(
    @PrimaryKey val id: String,
    val estimateId: String,
    val productId: String,
    val srNo: Int,
    val itemCode: String,
    val itemName: String,
    val quantity: Int,
    val unit: String,
    val purchaseRate: Double,
    val sellingRate: Double,
    val discountPercent: Double,
    val discountAmount: Double,
    val gstPercent: Double,
    val lineTotal: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "invoices",
    indices = [
        Index(value = ["invoiceNumber"], unique = true)
    ]
)
data class LocalInvoice(
    @PrimaryKey override val id: String,
    val estimateId: String,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String = "",
    val customerAddress: String = "",
    val date: String,
    val time: String,
    val status: String,
    val subtotal: Double,
    val discountTotal: Double,
    val gstTotal: Double,
    val grandTotal: Double,
    val paymentMethod: String?,
    override val createdAt: Long,
    override val updatedAt: Long
) : BaseEntity

@Entity(
    tableName = "invoice_items",
    indices = [
        Index(value = ["invoiceId"]),
        Index(value = ["productId"])
    ]
)
data class LocalInvoiceItem(
    @PrimaryKey val id: String,
    val invoiceId: String,
    val productId: String,
    val srNo: Int,
    val itemCode: String,
    val itemName: String,
    val quantity: Int,
    val unit: String,
    val purchaseRate: Double,
    val sellingRate: Double,
    val discountPercent: Double,
    val discountAmount: Double,
    val gstPercent: Double,
    val lineTotal: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
