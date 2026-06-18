package com.tirupati.pos.feature.sales.presentation.state

import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.Invoice
import com.tirupati.pos.feature.products.domain.model.Product

data class EstimateUiState(
    val estimates: List<Estimate> = emptyList(),
    val currentEstimate: Estimate? = null,
    val searchProductsQuery: String = "",
    val searchProductsResults: List<Product> = emptyList(),
    
    // Draft estimate state (building process)
    val selectedItems: List<EstimateItem> = emptyList(),
    val activeItemForEditing: EstimateItem? = null,
    val showRowEditorBottomSheet: Boolean = false,
    
    // Stock quantity warnings (map of itemCode -> warning message)
    val stockWarnings: Map<String, String> = emptyMap(),

    // Master data
    val companies: List<com.tirupati.pos.feature.products.domain.model.Company> = emptyList(),

    // Quick Create Product dialog state
    val showCreateProductDialog: Boolean = false,
    val quickProductCompanyId: String = "",
    val quickProductItemCode: String = "",
    val quickProductItemName: String = "",
    val quickProductUnit: String = "Pcs",
    val quickProductPurchaseRate: String = "",
    val quickProductSellingPrice: String = "",
    val quickProductStockQuantity: String = "",

    // Duplicate Product Warning dialog state
    val showDuplicateProductWarning: Boolean = false,
    val duplicateProductWarningMessage: String = "",

    // Quick Create Company dialog state
    val showCreateCompanyDialog: Boolean = false,
    val quickCompanyName: String = "",

    // Print Dialog state
    val showPrintDialog: Boolean = false,
    val showPrintPreview: Boolean = false,
    val printCustomerName: String = "",
    val printPhoneNumber: String = "",
    val printAddress: String = "",

    // Bill Summaries
    val subtotal: Double = 0.0,
    val discountTotal: Double = 0.0,
    val gstTotal: Double = 0.0,
    val grandTotal: Double = 0.0,
    
    // Invoice mapping
    val currentInvoice: Invoice? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface EstimateEvent {
    data object StartNewEstimate : EstimateEvent
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

    // Quick Product Dialog
    data object ClickCreateProduct : EstimateEvent
    data class UpdateQuickProductCompanyId(val companyId: String) : EstimateEvent
    data class UpdateQuickProductItemCode(val itemCode: String) : EstimateEvent
    data class UpdateQuickProductItemName(val itemName: String) : EstimateEvent
    data class UpdateQuickProductUnit(val unit: String) : EstimateEvent
    data class UpdateQuickProductPurchaseRate(val rate: String) : EstimateEvent
    data class UpdateQuickProductSellingPrice(val price: String) : EstimateEvent
    data class UpdateQuickProductStockQuantity(val qty: String) : EstimateEvent
    data object SaveQuickProduct : EstimateEvent
    data object ConfirmSaveDuplicateProduct : EstimateEvent
    data object DismissDuplicateProductWarning : EstimateEvent
    data object DismissQuickCreateProductDialog : EstimateEvent

    // Quick Company Dialog
    data object ClickCreateCompany : EstimateEvent
    data class UpdateQuickCompanyName(val name: String) : EstimateEvent
    data object SaveQuickCompany : EstimateEvent
    data object DismissQuickCreateCompanyDialog : EstimateEvent

    // Print Flow
    data class UpdatePrintCustomerName(val name: String) : EstimateEvent
    data class UpdatePrintPhoneNumber(val phone: String) : EstimateEvent
    data class UpdatePrintAddress(val address: String) : EstimateEvent
    data object ClickPrintEstimate : EstimateEvent
    data object DismissPrintDialog : EstimateEvent
    data object ConfirmPrintEstimate : EstimateEvent
    data object ClosePrintPreview : EstimateEvent

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
