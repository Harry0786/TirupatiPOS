package com.tirupati.pos.feature.sales.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tirupati.pos.core.database.BaseEntity

@Entity(tableName = "estimates")
data class LocalEstimate(
    @PrimaryKey override val id: String,
    val estimateNumber: String,
    val customerName: String,
    val date: String,
    val time: String,
    val status: String,
    val subtotal: Double,
    val itemDiscount: Double,
    val billDiscount: Double,
    val gstTotal: Double,
    val grandTotal: Double,
    override val createdAt: Long,
    override val updatedAt: Long
) : BaseEntity

@Entity(tableName = "estimate_items")
data class LocalEstimateItem(
    @PrimaryKey val id: String,
    val estimateId: String,
    val srNo: Int,
    val itemCode: String,
    val itemName: String,
    val quantity: Int,
    val unit: String,
    val rate: Double,
    val discountPercent: Double,
    val discountAmount: Double,
    val gstPercent: Double,
    val amount: Double
)

@Entity(tableName = "invoices")
data class LocalInvoice(
    @PrimaryKey override val id: String,
    val estimateId: String,
    val invoiceNumber: String,
    val customerName: String,
    val date: String,
    val time: String,
    val status: String,
    val subtotal: Double,
    val itemDiscount: Double,
    val billDiscount: Double,
    val gstTotal: Double,
    val grandTotal: Double,
    val paymentMethod: String?,
    override val createdAt: Long,
    override val updatedAt: Long
) : BaseEntity

@Entity(tableName = "invoice_items")
data class LocalInvoiceItem(
    @PrimaryKey val id: String,
    val invoiceId: String,
    val srNo: Int,
    val itemCode: String,
    val itemName: String,
    val quantity: Int,
    val unit: String,
    val rate: Double,
    val discountPercent: Double,
    val discountAmount: Double,
    val gstPercent: Double,
    val amount: Double
)

@Entity(tableName = "local_products")
data class LocalProduct(
    @PrimaryKey override val id: String,
    val itemCode: String,
    val itemName: String,
    val unit: String,
    val sellingPrice: Double,
    val gstPercent: Double,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis()
) : BaseEntity
