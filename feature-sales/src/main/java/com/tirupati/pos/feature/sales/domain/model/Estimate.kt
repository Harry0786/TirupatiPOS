package com.tirupati.pos.feature.sales.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class EstimateStatus {
    DRAFT,
    APPROVED,
    INVOICE,
    PAID,
    SYNCED
}

@Serializable
enum class InvoiceStatus {
    PENDING,
    PAID,
    SYNCED
}

@Serializable
data class Product(
    val id: String,
    val itemCode: String,
    val itemName: String,
    val unit: String,
    val sellingPrice: Double,
    val gstPercent: Double,
    val stock: Int = 100 // Default stock placeholder
)

@Serializable
data class EstimateItem(
    val id: String,
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
    val amount: Double = calculateLineTotal(quantity, rate, discountPercent, discountAmount, gstPercent)
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
    val date: String,
    val time: String,
    val status: EstimateStatus,
    val subtotal: Double,
    val itemDiscount: Double,
    val billDiscount: Double = 0.0,
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

@Serializable
data class Invoice(
    val id: String,
    val estimateId: String,
    val invoiceNumber: String,
    val customerName: String,
    val date: String,
    val time: String,
    val status: InvoiceStatus,
    val subtotal: Double,
    val itemDiscount: Double,
    val billDiscount: Double = 0.0,
    val gstTotal: Double,
    val grandTotal: Double,
    val paymentMethod: String? = null, // CASH, UPI, CARD, BANK_TRANSFER
    val items: List<InvoiceItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
