package com.tirupati.pos.feature.sales.data.mapper

import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.EstimateStatus
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.sales.domain.model.InvoiceItem
import com.tirupati.pos.feature.sales.domain.model.InvoiceStatus
import com.tirupati.pos.feature.sales.domain.model.Product
import com.tirupati.pos.feature.sales.data.local.LocalEstimate
import com.tirupati.pos.feature.sales.data.local.LocalEstimateItem
import com.tirupati.pos.feature.sales.data.local.LocalInvoice
import com.tirupati.pos.feature.sales.data.local.LocalInvoiceItem
import com.tirupati.pos.feature.sales.data.local.LocalProduct

fun LocalEstimate.toDomain(items: List<LocalEstimateItem>): Estimate {
    return Estimate(
        id = id,
        estimateNumber = estimateNumber,
        customerName = customerName,
        date = date,
        time = time,
        status = try { EstimateStatus.valueOf(status) } catch (e: Exception) { EstimateStatus.DRAFT },
        subtotal = subtotal,
        itemDiscount = itemDiscount,
        billDiscount = billDiscount,
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
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        rate = rate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        amount = amount
    )
}

fun Estimate.toLocal(): LocalEstimate {
    return LocalEstimate(
        id = id,
        estimateNumber = estimateNumber,
        customerName = customerName,
        date = date,
        time = time,
        status = status.name,
        subtotal = subtotal,
        itemDiscount = itemDiscount,
        billDiscount = billDiscount,
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
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        rate = rate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        amount = amount
    )
}

fun LocalInvoice.toDomain(items: List<LocalInvoiceItem>): Invoice {
    return Invoice(
        id = id,
        estimateId = estimateId,
        invoiceNumber = invoiceNumber,
        customerName = customerName,
        date = date,
        time = time,
        status = try { InvoiceStatus.valueOf(status) } catch (e: Exception) { InvoiceStatus.PENDING },
        subtotal = subtotal,
        itemDiscount = itemDiscount,
        billDiscount = billDiscount,
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
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        rate = rate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        amount = amount
    )
}

fun Invoice.toLocal(): LocalInvoice {
    return LocalInvoice(
        id = id,
        estimateId = estimateId,
        invoiceNumber = invoiceNumber,
        customerName = customerName,
        date = date,
        time = time,
        status = status.name,
        subtotal = subtotal,
        itemDiscount = itemDiscount,
        billDiscount = billDiscount,
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
        srNo = srNo,
        itemCode = itemCode,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        rate = rate,
        discountPercent = discountPercent,
        discountAmount = discountAmount,
        gstPercent = gstPercent,
        amount = amount
    )
}

fun LocalProduct.toDomain(): Product {
    return Product(
        id = id,
        itemCode = itemCode,
        itemName = itemName,
        unit = unit,
        sellingPrice = sellingPrice,
        gstPercent = gstPercent
    )
}

fun Product.toLocal(): LocalProduct {
    return LocalProduct(
        id = id,
        itemCode = itemCode,
        itemName = itemName,
        unit = unit,
        sellingPrice = sellingPrice,
        gstPercent = gstPercent
    )
}
