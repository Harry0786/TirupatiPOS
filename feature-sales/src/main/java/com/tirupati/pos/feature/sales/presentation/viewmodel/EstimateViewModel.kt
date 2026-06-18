package com.tirupati.pos.feature.sales.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirupati.pos.feature.sales.domain.model.Estimate
import com.tirupati.pos.feature.sales.domain.model.EstimateItem
import com.tirupati.pos.feature.sales.domain.model.EstimateStatus
import com.tirupati.pos.feature.sales.domain.model.Product
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
    private val salesRepository: SalesRepository
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
        // Run initial empty product search to populate placeholders
        onEvent(EstimateEvent.SearchProducts(""))
    }

    fun onEvent(event: EstimateEvent) {
        when (event) {
            is EstimateEvent.SearchProducts -> {
                _state.update { it.copy(searchProductsQuery = event.query) }
                viewModelScope.launch {
                    val results = searchProductsUseCase(event.query)
                    _state.update { it.copy(searchProductsResults = results) }
                }
            }
            is EstimateEvent.AddProductToEstimate -> {
                val product = event.product
                val existingItemIndex = _state.value.selectedItems.indexOfFirst { it.itemCode == product.itemCode }
                
                _state.update { currentState ->
                    val newList = currentState.selectedItems.toMutableList()
                    if (existingItemIndex != -1) {
                        val existing = newList[existingItemIndex]
                        val updated = existing.copy(
                            quantity = existing.quantity + 1,
                            amount = EstimateItem.calculateLineTotal(
                                existing.quantity + 1,
                                existing.rate,
                                existing.discountPercent,
                                existing.discountAmount,
                                existing.gstPercent
                            )
                        )
                        newList[existingItemIndex] = updated
                    } else {
                        val newItem = EstimateItem(
                            id = UUID.randomUUID().toString(),
                            estimateId = "",
                            srNo = newList.size + 1,
                            itemCode = product.itemCode,
                            itemName = product.itemName,
                            quantity = 1,
                            unit = product.unit,
                            rate = product.sellingPrice,
                            discountPercent = 0.0,
                            discountAmount = 0.0,
                            gstPercent = product.gstPercent
                        )
                        newList.add(newItem)
                    }
                    currentState.copy(selectedItems = newList).recalculateTotals()
                }
            }
            is EstimateEvent.EditItemClicked -> {
                _state.update { it.copy(activeItemForEditing = event.item, showRowEditorBottomSheet = true) }
            }
            is EstimateEvent.UpdateItemInEstimate -> {
                _state.update { currentState ->
                    val newList = currentState.selectedItems.map { item ->
                        if (item.id == event.itemId) {
                            item.copy(
                                quantity = event.quantity,
                                rate = event.rate,
                                discountPercent = event.discountPercent,
                                discountAmount = event.discountAmount,
                                gstPercent = event.gstPercent,
                                amount = EstimateItem.calculateLineTotal(
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
                        activeItemForEditing = null
                    ).recalculateTotals()
                }
            }
            is EstimateEvent.RemoveItemFromEstimate -> {
                _state.update { currentState ->
                    val newList = currentState.selectedItems.filter { it.id != event.itemId }
                        .mapIndexed { index, item ->
                            item.copy(srNo = index + 1)
                        }
                    currentState.copy(
                        selectedItems = newList,
                        showRowEditorBottomSheet = false,
                        activeItemForEditing = null
                    ).recalculateTotals()
                }
            }
            is EstimateEvent.DismissRowEditor -> {
                _state.update { it.copy(showRowEditorBottomSheet = false, activeItemForEditing = null) }
            }
            is EstimateEvent.SetBillDiscount -> {
                _state.update { it.copy(billDiscount = event.amount).recalculateTotals() }
            }
            is EstimateEvent.ClickCreateProduct -> {
                _state.update {
                    it.copy(
                        showCreateProductDialog = true,
                        quickProductItemCode = it.searchProductsQuery,
                        quickProductItemName = "",
                        quickProductSellingPrice = "",
                        quickProductGstPercent = 18.0
                    )
                }
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
            is EstimateEvent.UpdateQuickProductSellingPrice -> {
                _state.update { it.copy(quickProductSellingPrice = event.price) }
            }
            is EstimateEvent.UpdateQuickProductGstPercent -> {
                _state.update { it.copy(quickProductGstPercent = event.gst) }
            }
            is EstimateEvent.SaveQuickProduct -> {
                val code = _state.value.quickProductItemCode
                val name = _state.value.quickProductItemName
                val unit = _state.value.quickProductUnit
                val price = _state.value.quickProductSellingPrice.toDoubleOrNull() ?: 0.0
                val gst = _state.value.quickProductGstPercent

                if (code.isBlank() || name.isBlank()) {
                    viewModelScope.launch {
                        _effect.emit(EstimateEffect.ShowError("Code and Name cannot be empty"))
                    }
                    return
                }

                viewModelScope.launch {
                    val product = createProductUseCase(code, name, unit, price, gst)
                    _state.update { it.copy(showCreateProductDialog = false) }
                    onEvent(EstimateEvent.SearchProducts(_state.value.searchProductsQuery))
                    onEvent(EstimateEvent.AddProductToEstimate(product))
                }
            }
            is EstimateEvent.DismissQuickCreateProductDialog -> {
                _state.update { it.copy(showCreateProductDialog = false) }
            }
            is EstimateEvent.SaveDraftEstimate -> {
                if (_state.value.selectedItems.isEmpty()) {
                    viewModelScope.launch {
                        _effect.emit(EstimateEffect.ShowError("No items added to estimate"))
                    }
                    return
                }
                viewModelScope.launch {
                    val timestamp = System.currentTimeMillis()
                    val format = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    val suffix = format.format(Date(timestamp)).takeLast(6)
                    val estimateNumber = "EST-2026-$suffix"

                    val estimate = Estimate(
                        id = UUID.randomUUID().toString(),
                        estimateNumber = estimateNumber,
                        customerName = "Walk-In Customer",
                        date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp)),
                        time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp)),
                        status = EstimateStatus.DRAFT,
                        subtotal = _state.value.subtotal,
                        itemDiscount = _state.value.itemDiscount,
                        billDiscount = _state.value.billDiscount,
                        gstTotal = _state.value.gstTotal,
                        grandTotal = _state.value.grandTotal
                    )

                    saveEstimateUseCase(estimate, _state.value.selectedItems)
                    _state.update {
                        it.copy(
                            selectedItems = emptyList(),
                            billDiscount = 0.0
                        ).recalculateTotals()
                    }
                    _effect.emit(EstimateEffect.NavigateToEstimatesList)
                }
            }
            is EstimateEvent.SaveAndConvertToInvoice -> {
                if (_state.value.selectedItems.isEmpty()) {
                    viewModelScope.launch {
                        _effect.emit(EstimateEffect.ShowError("No items added to estimate"))
                    }
                    return
                }
                viewModelScope.launch {
                    val timestamp = System.currentTimeMillis()
                    val format = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    val suffix = format.format(Date(timestamp)).takeLast(6)
                    val estimateNumber = "EST-2026-$suffix"
                    val estimateId = UUID.randomUUID().toString()

                    val estimate = Estimate(
                        id = estimateId,
                        estimateNumber = estimateNumber,
                        customerName = "Walk-In Customer",
                        date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp)),
                        time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp)),
                        status = EstimateStatus.DRAFT,
                        subtotal = _state.value.subtotal,
                        itemDiscount = _state.value.itemDiscount,
                        billDiscount = _state.value.billDiscount,
                        gstTotal = _state.value.gstTotal,
                        grandTotal = _state.value.grandTotal
                    )

                    saveEstimateUseCase(estimate, _state.value.selectedItems)
                    
                    val invoice = convertToInvoiceUseCase(estimateId)
                    _state.update {
                        it.copy(
                            selectedItems = emptyList(),
                            billDiscount = 0.0
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

    fun loadEstimateDetails(id: String) {
        viewModelScope.launch {
            getEstimateDetailsUseCase(id).collect { estimate ->
                _state.update { it.copy(currentEstimate = estimate) }
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

    private fun EstimateUiState.recalculateTotals(): EstimateUiState {
        val sub = selectedItems.sumOf { it.quantity * it.rate }
        val itemDisc = selectedItems.sumOf {
            val raw = it.quantity * it.rate
            val pDisc = raw * (it.discountPercent / 100.0)
            pDisc + it.discountAmount
        }
        val gst = selectedItems.sumOf {
            val raw = it.quantity * it.rate
            val pDisc = raw * (it.discountPercent / 100.0)
            val taxable = (raw - pDisc - it.discountAmount).coerceAtLeast(0.0)
            taxable * (it.gstPercent / 100.0)
        }
        val grand = (sub - itemDisc - billDiscount + gst).coerceAtLeast(0.0)
        return this.copy(
            subtotal = sub,
            itemDiscount = itemDisc,
            gstTotal = gst,
            grandTotal = grand
        )
    }
}
