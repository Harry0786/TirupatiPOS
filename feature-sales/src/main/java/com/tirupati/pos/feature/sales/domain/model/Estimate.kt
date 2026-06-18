package com.tirupati.pos.feature.sales.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class EstimateStatus {
    DRAFT,
    PRINTED,
    CONVERTED,
    CANCELLED
}

@Serializable
enum class InvoiceStatus {
    PENDING,
    PAID,
    SYNCED
}

@Serializable
data class EstimateItem(
    val id: String,
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
    val lineTotal: Double = calculateLineTotal(quantity, sellingRate, discountPercent, discountAmount, gstPercent),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun calculateLineTotal(
            quantity: Int,
            rate: Double,
            discountPercent: Double,
            discountAmount: Double,
            gstPercent: Double
        ): Double {
            val rawSubtotal = quantity * rate
            val afterPercent = rawSubtotal * (1 - (discountPercent / 100.0))
            val afterFixed = (afterPercent - discountAmount).coerceAtLeast(0.0)
            val gst = afterFixed * (gstPercent / 100.0)
            return afterFixed + gst
        }
    }
}

@Serializable
data class Estimate(
    val id: String,
    val estimateNumber: String,
    val customerName: String,
    val customerPhone: String = "",
    val customerAddress: String = "",
    val date: String,
    val time: String,
    val status: EstimateStatus,
    val subtotal: Double,
    val discountTotal: Double,
    val gstTotal: Double,
    val grandTotal: Double,
    val items: List<EstimateItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class InvoiceItem(
    val id: String,
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

@Serializable
data class Invoice(
    val id: String,
    val estimateId: String,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String = "",
    val customerAddress: String = "",
    val date: String,
    val time: String,
    val status: InvoiceStatus,
    val subtotal: Double,
    val discountTotal: Double,
    val gstTotal: Double,
    val grandTotal: Double,
    val paymentMethod: String? = null, // CASH, UPI, CARD, BANK_TRANSFER
    val items: List<InvoiceItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
