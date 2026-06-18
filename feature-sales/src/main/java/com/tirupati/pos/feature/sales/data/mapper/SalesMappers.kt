package com.tirupati.pos.feature.sales.data.mapper

import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.EstimateStatus
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.sales.domain.model.InvoiceItem
import com.tirupati.pos.feature.sales.domain.model.InvoiceStatus
import com.tirupati.pos.feature.sales.data.local.LocalEstimate
import com.tirupati.pos.feature.sales.data.local.LocalEstimateItem
import com.tirupati.pos.feature.sales.data.local.LocalInvoice
import com.tirupati.pos.feature.sales.data.local.LocalInvoiceItem

fun LocalEstimate.toDomain(items: List<LocalEstimateItem>): Estimate {
    return Estimate(
        id = id,
        estimateNumber = estimateNumber,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        date = date,
        time = time,
        status = try { EstimateStatus.valueOf(status) } catch (e: Exception) { EstimateStatus.DRAFT },
        subtotal = subtotal,
        discountTotal = discountTotal,
        gstTotal = gstTotal,
        grandTotal = grandTotal,
        items = items.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun LocalEstimateItem.toDomain(): EstimateItem {
    return EstimateItem(
        id = id,
        estimateId = estimateId,
        productId = productId,
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        purchaseRate = purchaseRate,
        sellingRate = sellingRate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        lineTotal = lineTotal,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Estimate.toLocal(): LocalEstimate {
    return LocalEstimate(
        id = id,
        estimateNumber = estimateNumber,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        date = date,
        time = time,
        status = status.name,
        subtotal = subtotal,
        discountTotal = discountTotal,
        gstTotal = gstTotal,
        grandTotal = grandTotal,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun EstimateItem.toLocal(): LocalEstimateItem {
    return LocalEstimateItem(
        id = id,
        estimateId = estimateId,
        productId = productId,
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        purchaseRate = purchaseRate,
        sellingRate = sellingRate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        lineTotal = lineTotal,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun LocalInvoice.toDomain(items: List<LocalInvoiceItem>): Invoice {
    return Invoice(
        id = id,
        estimateId = estimateId,
        invoiceNumber = invoiceNumber,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        date = date,
        time = time,
        status = try { InvoiceStatus.valueOf(status) } catch (e: Exception) { InvoiceStatus.PENDING },
        subtotal = subtotal,
        discountTotal = discountTotal,
        gstTotal = gstTotal,
        grandTotal = grandTotal,
        paymentMethod = paymentMethod,
        items = items.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun LocalInvoiceItem.toDomain(): InvoiceItem {
    return InvoiceItem(
        id = id,
        invoiceId = invoiceId,
        productId = productId,
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        purchaseRate = purchaseRate,
        sellingRate = sellingRate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        lineTotal = lineTotal,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Invoice.toLocal(): LocalInvoice {
    return LocalInvoice(
        id = id,
        estimateId = estimateId,
        invoiceNumber = invoiceNumber,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        date = date,
        time = time,
        status = status.name,
        subtotal = subtotal,
        discountTotal = discountTotal,
        gstTotal = gstTotal,
        grandTotal = grandTotal,
        paymentMethod = paymentMethod,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun InvoiceItem.toLocal(invoiceId: String): LocalInvoiceItem {
    return LocalInvoiceItem(
        id = id,
        invoiceId = invoiceId,
        productId = productId,
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        purchaseRate = purchaseRate,
        sellingRate = sellingRate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        lineTotal = lineTotal,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
