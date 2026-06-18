package com.tirupati.pos.feature.sales.domain.usecase

import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.EstimateStatus
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.sales.domain.model.InvoiceStatus
import com.tirupati.pos.feature.sales.domain.model.Product
import com.tirupati.pos.feature.sales.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class GetEstimatesUseCase @Inject constructor(
    private val repository: SalesRepository
) {
    operator fun invoke(): Flow<List<Estimate>> = repository.observeEstimates()
}

class GetEstimateDetailsUseCase @Inject constructor(
    private val repository: SalesRepository
) {
    operator fun invoke(id: String): Flow<Estimate?> = repository.observeEstimate(id)
}

class SaveEstimateUseCase @Inject constructor(
    private val repository: SalesRepository
) {
    suspend operator fun invoke(estimate: Estimate, items: List<EstimateItem>) {
        repository.saveEstimate(estimate, items)
    }
}

class SearchProductsUseCase @Inject constructor(
    private val repository: SalesRepository
) {
    suspend operator fun invoke(query: String): List<Product> {
        return repository.searchProducts(query)
    }
}

class CreateProductUseCase @Inject constructor(
    private val repository: SalesRepository
) {
    suspend operator fun invoke(
        itemCode: String,
        itemName: String,
        unit: String,
        sellingPrice: Double,
        gstPercent: Double
    ): Product {
        val product = Product(
            id = UUID.randomUUID().toString(),
            itemCode = itemCode,
            itemName = itemName,
            unit = unit,
            sellingPrice = sellingPrice,
            gstPercent = gstPercent
        )
        repository.saveProduct(product)
        return product
    }
}

class ConvertToInvoiceUseCase @Inject constructor(
    private val repository: SalesRepository
) {
    suspend operator fun invoke(estimateId: String): Invoice? {
        val estimate = repository.getEstimate(estimateId) ?: return null
        
        // Generate Invoice Number
        val timestamp = System.currentTimeMillis()
        val format = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val suffix = format.format(Date(timestamp)).takeLast(6)
        val invoiceNumber = "INV-2026-$suffix"

        val invoice = Invoice(
            id = UUID.randomUUID().toString(),
            estimateId = estimate.id,
            invoiceNumber = invoiceNumber,
            customerName = estimate.customerName,
            date = estimate.date,
            time = estimate.time,
            status = InvoiceStatus.PENDING,
            subtotal = estimate.subtotal,
            itemDiscount = estimate.itemDiscount,
            billDiscount = estimate.billDiscount,
            gstTotal = estimate.gstTotal,
            grandTotal = estimate.grandTotal,
            items = estimate.items.map {
                com.tirupati.pos.feature.sales.domain.model.InvoiceItem(
                    id = UUID.randomUUID().toString(),
                    invoiceId = "", // set in implementation repo mapping if needed
                    srNo = it.srNo,
                    itemCode = it.itemCode,
                    itemName = it.itemName,
                    quantity = it.quantity,
                    unit = it.unit,
                    rate = it.rate,
                    discountPercent = it.discountPercent,
                    discountAmount = it.discountAmount,
                    gstPercent = it.gstPercent,
                    amount = it.amount
                )
            }
        )

        repository.saveInvoice(invoice)
        // Also update the original estimate status to INVOICE
        repository.updateEstimateStatus(estimateId, EstimateStatus.INVOICE.name)
        
        return invoice
    }
}
