package com.tirupati.pos.feature.sales.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.EstimateStatus
import com.tirupati.pos.feature.products.domain.model.Product
import com.tirupati.pos.feature.sales.domain.usecase.GetEstimatesUseCase
import com.tirupati.pos.feature.sales.domain.usecase.GetEstimateDetailsUseCase
import com.tirupati.pos.feature.sales.domain.usecase.SaveEstimateUseCase
import com.tirupati.pos.feature.sales.domain.usecase.ConvertToInvoiceUseCase
import com.tirupati.pos.feature.sales.domain.usecase.SearchProductsUseCase
import com.tirupati.pos.feature.sales.domain.usecase.CreateProductUseCase
import com.tirupati.pos.feature.sales.domain.repository.SalesRepository
import com.tirupati.pos.feature.sales.presentation.state.EstimateEffect
import com.tirupati.pos.feature.sales.presentation.state.EstimateEvent
import com.tirupati.pos.feature.sales.presentation.state.EstimateUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EstimateViewModel @Inject constructor(
    private val getEstimatesUseCase: GetEstimatesUseCase,
    private val getEstimateDetailsUseCase: GetEstimateDetailsUseCase,
    private val saveEstimateUseCase: SaveEstimateUseCase,
    private val convertToInvoiceUseCase: ConvertToInvoiceUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val salesRepository: SalesRepository,
    private val productRepository: com.tirupati.pos.feature.products.domain.repository.ProductRepository,
    private val companyRepository: com.tirupati.pos.feature.products.domain.repository.CompanyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EstimateUiState())
    val state: StateFlow<EstimateUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EstimateEffect>()
    val effect: SharedFlow<EstimateEffect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            getEstimatesUseCase().collect { list ->
                _state.update { it.copy(estimates = list) }
            }
        }
        viewModelScope.launch {
            companyRepository.observeCompanies().collect { list ->
                _state.update { it.copy(companies = list) }
            }
        }
        onEvent(EstimateEvent.SearchProducts(""))
    }

    fun onEvent(event: EstimateEvent) {
        when (event) {
            is EstimateEvent.StartNewEstimate -> {
                val nextEstimateNumber = generateNextEstimateNumber(_state.value.estimates)
                val estimateId = UUID.randomUUID().toString()
                val timestamp = System.currentTimeMillis()
                
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

                val newEstimate = Estimate(
                    id = estimateId,
                    estimateNumber = nextEstimateNumber,
                    customerName = "Walk-In Customer",
                    customerPhone = "",
                    customerAddress = "",
                    date = dateStr,
                    time = timeStr,
                    status = EstimateStatus.DRAFT,
                    subtotal = 0.0,
                    discountTotal = 0.0,
                    gstTotal = 0.0,
                    grandTotal = 0.0,
                    items = emptyList()
                )

                _state.update {
                    it.copy(
                        currentEstimate = newEstimate,
                        selectedItems = emptyList(),
                        stockWarnings = emptyMap(),
                        printCustomerName = "",
                        printPhoneNumber = "",
                        printAddress = ""
                    ).recalculateTotals()
                }
            }
            is EstimateEvent.SearchProducts -> {
                _state.update { it.copy(searchProductsQuery = event.query) }
                viewModelScope.launch {
                    val results = salesRepository.searchProducts(event.query)
                    _state.update { it.copy(searchProductsResults = results) }
                }
            }
            is EstimateEvent.AddProductToEstimate -> {
                val product = event.product
                val existingItemIndex = _state.value.selectedItems.indexOfFirst { it.itemCode == product.itemCode }
                
                _state.update { currentState ->
                    val newList = currentState.selectedItems.toMutableList()
                    val newQty = if (existingItemIndex != -1) newList[existingItemIndex].quantity + 1 else 1

                    // Stock Availability Warning Check
                    val updatedWarnings = currentState.stockWarnings.toMutableMap()
                    if (newQty > product.stockQuantity) {
                        updatedWarnings[product.itemCode] = "⚠ Available Stock: ${product.stockQuantity.toInt()} | Requested Quantity: $newQty"
                    } else {
                        updatedWarnings.remove(product.itemCode)
                    }

                    if (existingItemIndex != -1) {
                        val existing = newList[existingItemIndex]
                        val updated = existing.copy(
                            quantity = newQty,
                            lineTotal = EstimateItem.calculateLineTotal(
                                newQty,
                                existing.sellingRate,
                                existing.discountPercent,
                                existing.discountAmount,
                                existing.gstPercent
                            )
                        )
                        newList[existingItemIndex] = updated
                    } else {
                        val newItem = EstimateItem(
                            id = UUID.randomUUID().toString(),
                            estimateId = currentState.currentEstimate?.id ?: "",
                            productId = product.id,
                            srNo = newList.size + 1,
                            itemCode = product.itemCode,
                            itemName = product.itemName,
                            quantity = 1,
                            unit = product.unit,
                            purchaseRate = product.purchaseRate,
                            sellingRate = product.sellingRate,
                            discountPercent = 0.0,
                            discountAmount = 0.0,
                            gstPercent = 18.0 // default GST of 18% per specs
                        )
                        newList.add(newItem)
                    }
                    currentState.copy(selectedItems = newList, stockWarnings = updatedWarnings).recalculateTotals()
                }
            }
            is EstimateEvent.EditItemClicked -> {
                _state.update { it.copy(activeItemForEditing = event.item, showRowEditorBottomSheet = true) }
            }
            is EstimateEvent.UpdateItemInEstimate -> {
                viewModelScope.launch {
                    val product = productRepository.getProduct(event.itemId) ?: productRepository.observeProducts().firstOrNull()?.find { it.id == event.itemId }
                    val stockQty = product?.stockQuantity ?: 999.0
                    val itemCode = _state.value.selectedItems.find { it.id == event.itemId }?.itemCode ?: ""

                    _state.update { currentState ->
                        val updatedWarnings = currentState.stockWarnings.toMutableMap()
                        if (event.quantity > stockQty && itemCode.isNotBlank()) {
                            updatedWarnings[itemCode] = "⚠ Available Stock: ${stockQty.toInt()} | Requested Quantity: ${event.quantity}"
                        } else if (itemCode.isNotBlank()) {
                            updatedWarnings.remove(itemCode)
                        }

                        val newList = currentState.selectedItems.map { item ->
                            if (item.id == event.itemId) {
                                item.copy(
                                    quantity = event.quantity,
                                    sellingRate = event.rate,
                                    discountPercent = event.discountPercent,
                                    discountAmount = event.discountAmount,
                                    gstPercent = event.gstPercent,
                                    lineTotal = EstimateItem.calculateLineTotal(
                                        event.quantity,
                                        event.rate,
                                        event.discountPercent,
                                        event.discountAmount,
                                        event.gstPercent
                                    )
                                )
                            } else {
                                item
                            }
                        }.mapIndexed { index, item ->
                            item.copy(srNo = index + 1)
                        }
                        currentState.copy(
                            selectedItems = newList,
                            showRowEditorBottomSheet = false,
                            activeItemForEditing = null,
                            stockWarnings = updatedWarnings
                        ).recalculateTotals()
                    }
                }
            }
            is EstimateEvent.RemoveItemFromEstimate -> {
                _state.update { currentState ->
                    val removedItemCode = currentState.selectedItems.find { it.id == event.itemId }?.itemCode ?: ""
                    val updatedWarnings = currentState.stockWarnings.toMutableMap()
                    updatedWarnings.remove(removedItemCode)

                    val newList = currentState.selectedItems.filter { it.id != event.itemId }
                        .mapIndexed { index, item ->
                            item.copy(srNo = index + 1)
                        }
                    currentState.copy(
                        selectedItems = newList,
                        showRowEditorBottomSheet = false,
                        activeItemForEditing = null,
                        stockWarnings = updatedWarnings
                    ).recalculateTotals()
                }
            }
            is EstimateEvent.DismissRowEditor -> {
                _state.update { it.copy(showRowEditorBottomSheet = false, activeItemForEditing = null) }
            }
            is EstimateEvent.ClickCreateProduct -> {
                val initialCompanyId = _state.value.companies.firstOrNull()?.id ?: ""
                _state.update {
                    it.copy(
                        showCreateProductDialog = true,
                        quickProductCompanyId = initialCompanyId,
                        quickProductItemCode = it.searchProductsQuery,
                        quickProductItemName = "",
                        quickProductUnit = "Pcs",
                        quickProductPurchaseRate = "",
                        quickProductSellingPrice = "",
                        quickProductStockQuantity = "100"
                    )
                }
            }
            is EstimateEvent.UpdateQuickProductCompanyId -> {
                _state.update { it.copy(quickProductCompanyId = event.companyId) }
            }
            is EstimateEvent.UpdateQuickProductItemCode -> {
                _state.update { it.copy(quickProductItemCode = event.itemCode) }
            }
            is EstimateEvent.UpdateQuickProductItemName -> {
                _state.update { it.copy(quickProductItemName = event.itemName) }
            }
            is EstimateEvent.UpdateQuickProductUnit -> {
                _state.update { it.copy(quickProductUnit = event.unit) }
            }
            is EstimateEvent.UpdateQuickProductPurchaseRate -> {
                _state.update { it.copy(quickProductPurchaseRate = event.rate) }
            }
            is EstimateEvent.UpdateQuickProductSellingPrice -> {
                _state.update { it.copy(quickProductSellingPrice = event.price) }
            }
            is EstimateEvent.UpdateQuickProductStockQuantity -> {
                _state.update { it.copy(quickProductStockQuantity = event.qty) }
            }
            is EstimateEvent.SaveQuickProduct -> {
                val code = _state.value.quickProductItemCode
                val name = _state.value.quickProductItemName

                if (code.isBlank() || name.isBlank()) {
                    viewModelScope.launch {
                        _effect.emit(EstimateEffect.ShowError("Code and Name cannot be empty"))
                    }
                    return
                }

                viewModelScope.launch {
                    // Check for duplicate product
                    val existing = productRepository.searchProducts(code) + productRepository.searchProducts(name)
                    val duplicate = existing.firstOrNull {
                        it.itemCode.equals(code, ignoreCase = true) || it.itemName.equals(name, ignoreCase = true)
                    }

                    if (duplicate != null) {
                        _state.update {
                            it.copy(
                                showDuplicateProductWarning = true,
                                duplicateProductWarningMessage = "Possible Duplicate Product Found: '${duplicate.itemName}' (${duplicate.itemCode})."
                            )
                        }
                    } else {
                        performSaveQuickProduct()
                    }
                }
            }
            is EstimateEvent.ConfirmSaveDuplicateProduct -> {
                _state.update { it.copy(showDuplicateProductWarning = false) }
                performSaveQuickProduct()
            }
            is EstimateEvent.DismissDuplicateProductWarning -> {
                _state.update { it.copy(showDuplicateProductWarning = false) }
            }
            is EstimateEvent.DismissQuickCreateProductDialog -> {
                _state.update { it.copy(showCreateProductDialog = false) }
            }
            is EstimateEvent.ClickCreateCompany -> {
                _state.update { it.copy(showCreateCompanyDialog = true, quickCompanyName = "") }
            }
            is EstimateEvent.UpdateQuickCompanyName -> {
                _state.update { it.copy(quickCompanyName = event.name) }
            }
            is EstimateEvent.SaveQuickCompany -> {
                val name = _state.value.quickCompanyName
                if (name.isBlank()) {
                    viewModelScope.launch {
                        _effect.emit(EstimateEffect.ShowError("Company Name cannot be empty"))
                    }
                    return
                }
                viewModelScope.launch {
                    val newCompany = com.tirupati.pos.feature.products.domain.model.Company(
                        id = UUID.randomUUID().toString(),
                        name = name
                    )
                    companyRepository.saveCompany(newCompany)
                    _state.update {
                        it.copy(
                            showCreateCompanyDialog = false,
                            quickProductCompanyId = newCompany.id
                        )
                    }
                }
            }
            is EstimateEvent.DismissQuickCreateCompanyDialog -> {
                _state.update { it.copy(showCreateCompanyDialog = false) }
            }
            is EstimateEvent.UpdatePrintCustomerName -> {
                _state.update { it.copy(printCustomerName = event.name) }
            }
            is EstimateEvent.UpdatePrintPhoneNumber -> {
                _state.update { it.copy(printPhoneNumber = event.phone) }
            }
            is EstimateEvent.UpdatePrintAddress -> {
                _state.update { it.copy(printAddress = event.address) }
            }
            is EstimateEvent.ClickPrintEstimate -> {
                _state.update {
                    it.copy(
                        showPrintDialog = true,
                        printCustomerName = it.currentEstimate?.customerName ?: "Walk-In Customer",
                        printPhoneNumber = it.currentEstimate?.customerPhone ?: "",
                        printAddress = it.currentEstimate?.customerAddress ?: ""
                    )
                }
            }
            is EstimateEvent.DismissPrintDialog -> {
                _state.update { it.copy(showPrintDialog = false) }
            }
            is EstimateEvent.ConfirmPrintEstimate -> {
                val activeEstimate = _state.value.currentEstimate ?: return
                val custName = if (_state.value.printCustomerName.isNotBlank()) _state.value.printCustomerName else "Walk-In Customer"
                val updatedEstimate = activeEstimate.copy(
                    customerName = custName,
                    customerPhone = _state.value.printPhoneNumber,
                    customerAddress = _state.value.printAddress,
                    status = EstimateStatus.PRINTED,
                    subtotal = _state.value.subtotal,
                    discountTotal = _state.value.discountTotal,
                    gstTotal = _state.value.gstTotal,
                    grandTotal = _state.value.grandTotal,
                    updatedAt = System.currentTimeMillis()
                )
                viewModelScope.launch {
                    saveEstimateUseCase(updatedEstimate, _state.value.selectedItems)
                    _state.update {
                        it.copy(
                            currentEstimate = updatedEstimate,
                            showPrintDialog = false,
                            showPrintPreview = true
                        )
                    }
                }
            }
            is EstimateEvent.ClosePrintPreview -> {
                _state.update {
                    it.copy(
                        showPrintPreview = false,
                        selectedItems = emptyList()
                    ).recalculateTotals()
                }
                viewModelScope.launch {
                    _effect.emit(EstimateEffect.NavigateToEstimatesList)
                }
            }
            is EstimateEvent.SaveDraftEstimate -> {
                val activeEstimate = _state.value.currentEstimate ?: return
                viewModelScope.launch {
                    val updatedEstimate = activeEstimate.copy(
                        status = EstimateStatus.DRAFT,
                        customerName = if (_state.value.printCustomerName.isNotBlank()) _state.value.printCustomerName else activeEstimate.customerName,
                        customerPhone = _state.value.printPhoneNumber,
                        customerAddress = _state.value.printAddress,
                        subtotal = _state.value.subtotal,
                        discountTotal = _state.value.discountTotal,
                        gstTotal = _state.value.gstTotal,
                        grandTotal = _state.value.grandTotal,
                        updatedAt = System.currentTimeMillis()
                    )

                    saveEstimateUseCase(updatedEstimate, _state.value.selectedItems)
                    _state.update {
                        it.copy(
                            selectedItems = emptyList()
                        ).recalculateTotals()
                    }
                    _effect.emit(EstimateEffect.NavigateToEstimatesList)
                }
            }
            is EstimateEvent.SaveAndConvertToInvoice -> {
                val activeEstimate = _state.value.currentEstimate ?: return
                viewModelScope.launch {
                    val updatedEstimate = activeEstimate.copy(
                        customerName = if (_state.value.printCustomerName.isNotBlank()) _state.value.printCustomerName else activeEstimate.customerName,
                        customerPhone = _state.value.printPhoneNumber,
                        customerAddress = _state.value.printAddress,
                        subtotal = _state.value.subtotal,
                        discountTotal = _state.value.discountTotal,
                        gstTotal = _state.value.gstTotal,
                        grandTotal = _state.value.grandTotal,
                        updatedAt = System.currentTimeMillis()
                    )
                    saveEstimateUseCase(updatedEstimate, _state.value.selectedItems)

                    val invoice = convertToInvoiceUseCase(updatedEstimate.id)
                    _state.update {
                        it.copy(
                            selectedItems = emptyList()
                        ).recalculateTotals()
                    }
                    if (invoice != null) {
                        _effect.emit(EstimateEffect.NavigateToInvoice(invoice.id))
                    } else {
                        _effect.emit(EstimateEffect.ShowError("Failed to convert estimate to invoice"))
                    }
                }
            }
            is EstimateEvent.ConvertToInvoice -> {
                viewModelScope.launch {
                    val invoice = convertToInvoiceUseCase(event.estimateId)
                    if (invoice != null) {
                        _effect.emit(EstimateEffect.NavigateToInvoice(invoice.id))
                    } else {
                        _effect.emit(EstimateEffect.ShowError("Failed to convert estimate to invoice"))
                    }
                }
            }
            is EstimateEvent.ProcessPayment -> {
                viewModelScope.launch {
                    salesRepository.updateInvoicePayment(event.invoiceId, event.method)
                    _effect.emit(EstimateEffect.NavigateToEstimatesList)
                }
            }
        }
    }

    private fun performSaveQuickProduct() {
        val companyId = _state.value.quickProductCompanyId
        val code = _state.value.quickProductItemCode
        val name = _state.value.quickProductItemName
        val unit = _state.value.quickProductUnit
        val purchase = _state.value.quickProductPurchaseRate.toDoubleOrNull() ?: 0.0
        val selling = _state.value.quickProductSellingPrice.toDoubleOrNull() ?: 0.0
        val stock = _state.value.quickProductStockQuantity.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            val newMasterProduct = com.tirupati.pos.feature.products.domain.model.Product(
                id = UUID.randomUUID().toString(),
                companyId = companyId,
                itemCode = code,
                itemName = name,
                unit = unit,
                purchaseRate = purchase,
                sellingRate = selling,
                stockQuantity = stock
            )
            productRepository.saveProduct(newMasterProduct)
            
            _state.update { it.copy(showCreateProductDialog = false) }
            onEvent(EstimateEvent.SearchProducts(_state.value.searchProductsQuery))
            onEvent(EstimateEvent.AddProductToEstimate(newMasterProduct))
        }
    }

    fun loadEstimateDetails(id: String) {
        viewModelScope.launch {
            getEstimateDetailsUseCase(id).collect { estimate ->
                _state.update { currentState ->
                    currentState.copy(
                        currentEstimate = estimate,
                        selectedItems = estimate?.items ?: emptyList(),
                        printCustomerName = estimate?.customerName ?: "",
                        printPhoneNumber = estimate?.customerPhone ?: "",
                        printAddress = estimate?.customerAddress ?: ""
                    ).recalculateTotals()
                }
            }
        }
    }

    fun loadInvoiceDetails(id: String) {
        viewModelScope.launch {
            salesRepository.observeInvoice(id).collect { invoice ->
                _state.update { it.copy(currentInvoice = invoice) }
            }
        }
    }

    fun clearActiveEstimate() {
        _state.update {
            it.copy(
                currentEstimate = null,
                selectedItems = emptyList(),
                stockWarnings = emptyMap(),
                printCustomerName = "",
                printPhoneNumber = "",
                printAddress = ""
            ).recalculateTotals()
        }
    }

    private fun generateNextEstimateNumber(estimates: List<Estimate>): String {
        var maxSerial = 0
        estimates.forEach { est ->
            val numStr = est.estimateNumber
            val lastDigits = numStr.substringAfterLast("-").toIntOrNull()
            if (lastDigits != null && lastDigits > maxSerial) {
                maxSerial = lastDigits
            } else {
                val digits = numStr.filter { it.isDigit() }.toIntOrNull()
                if (digits != null && digits > maxSerial) {
                    maxSerial = digits
                }
            }
        }
        val nextSerial = maxSerial + 1
        return String.format("EST-%04d", nextSerial)
    }

    private fun EstimateUiState.recalculateTotals(): EstimateUiState {
        val sub = selectedItems.sumOf { it.quantity * it.sellingRate }
        val disc = selectedItems.sumOf {
            val raw = it.quantity * it.sellingRate
            val pDisc = raw * (it.discountPercent / 100.0)
            pDisc + it.discountAmount
        }
        val gst = selectedItems.sumOf {
            val raw = it.quantity * it.sellingRate
            val pDisc = raw * (it.discountPercent / 100.0)
            val taxable = (raw - pDisc - it.discountAmount).coerceAtLeast(0.0)
            taxable * (it.gstPercent / 100.0)
        }
        val grand = (sub - disc + gst).coerceAtLeast(0.0)
        return this.copy(
            subtotal = sub,
            discountTotal = disc,
            gstTotal = gst,
            grandTotal = grand
        )
    }
}
