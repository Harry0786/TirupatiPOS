package com.tirupati.pos.feature.sales.presentation.state

import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.sales.domain.model.Product

data class EstimateUiState(
    val estimates: List<Estimate> = emptyList(),
    val currentEstimate: Estimate? = null,
    val searchProductsQuery: String = "",
    val searchProductsResults: List<Product> = emptyList(),
    
    // Draft estimate state (building process)
    val selectedItems: List<EstimateItem> = emptyList(),
    val activeItemForEditing: EstimateItem? = null,
    val showRowEditorBottomSheet: Boolean = false,
    
    // Quick Create Product dialog state
    val showCreateProductDialog: Boolean = false,
    val quickProductItemCode: String = "",
    val quickProductItemName: String = "",
    val quickProductUnit: String = "Pcs",
    val quickProductSellingPrice: String = "",
    val quickProductGstPercent: Double = 18.0,

    // Bill Summaries
    val subtotal: Double = 0.0,
    val itemDiscount: Double = 0.0,
    val billDiscount: Double = 0.0,
    val gstTotal: Double = 0.0,
    val grandTotal: Double = 0.0,
    
    // Invoice mapping
    val currentInvoice: Invoice? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface EstimateEvent {
    data class SearchProducts(val query: String) : EstimateEvent
    data class AddProductToEstimate(val product: Product) : EstimateEvent
    data class EditItemClicked(val item: EstimateItem) : EstimateEvent
    data class UpdateItemInEstimate(
        val itemId: String,
        val quantity: Int,
        val rate: Double,
        val discountPercent: Double,
        val discountAmount: Double,
        val gstPercent: Double
    ) : EstimateEvent
    data class RemoveItemFromEstimate(val itemId: String) : EstimateEvent
    data object DismissRowEditor : EstimateEvent
    
    // Bill Level Discount
    data class SetBillDiscount(val amount: Double) : EstimateEvent

    // Quick Product Dialog
    data object ClickCreateProduct : EstimateEvent
    data class UpdateQuickProductItemCode(val itemCode: String) : EstimateEvent
    data class UpdateQuickProductItemName(val itemName: String) : EstimateEvent
    data class UpdateQuickProductUnit(val unit: String) : EstimateEvent
    data class UpdateQuickProductSellingPrice(val price: String) : EstimateEvent
    data class UpdateQuickProductGstPercent(val gst: Double) : EstimateEvent
    data object SaveQuickProduct : EstimateEvent
    data object DismissQuickCreateProductDialog : EstimateEvent

    // Actions
    data object SaveDraftEstimate : EstimateEvent
    data object SaveAndConvertToInvoice : EstimateEvent
    data class ConvertToInvoice(val estimateId: String) : EstimateEvent
    data class ProcessPayment(val invoiceId: String, val method: String) : EstimateEvent
}

sealed interface EstimateEffect {
    data object NavigateBack : EstimateEffect
    data class NavigateToInvoice(val invoiceId: String) : EstimateEffect
    data class NavigateToPayment(val invoiceId: String) : EstimateEffect
    data object NavigateToEstimatesList : EstimateEffect
    data class ShowError(val message: String) : EstimateEffect
}
