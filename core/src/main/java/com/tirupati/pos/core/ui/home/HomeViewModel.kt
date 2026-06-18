package com.tirupati.pos.core.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncStatusManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val syncStatusManager: SyncStatusManager,
    private val syncManager: SyncManager,
    private val homeDataProvider: HomeDataProvider
) : ViewModel() {

    val syncUiState = syncStatusManager.syncUiState

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        viewModelScope.launch {
            homeDataProvider.observeSalesForDate(todayStr).collect { sales ->
                _homeUiState.update { it.copy(salesToday = String.format("%.2f", sales)) }
            }
        }
        viewModelScope.launch {
            homeDataProvider.observeBillsCountForDate(todayStr).collect { count ->
                _homeUiState.update { it.copy(billsToday = count) }
            }
        }
        viewModelScope.launch {
            homeDataProvider.observeItemsSoldCountForDate(todayStr).collect { count ->
                _homeUiState.update { it.copy(itemsSoldCount = count) }
            }
        }
        viewModelScope.launch {
            homeDataProvider.observeCustomersCountForDate(todayStr).collect { count ->
                _homeUiState.update { it.copy(customersCount = count) }
            }
        }
        viewModelScope.launch {
            homeDataProvider.observeRecentActivities(5).collect { items ->
                _homeUiState.update { it.copy(recentActivities = items) }
            }
        }
    }

    fun refreshStatus() {
        syncStatusManager.refreshBackendHealth()
        viewModelScope.launch {
            syncManager.requestSync()
        }
    }
    
    fun requestSync() {
        viewModelScope.launch {
            syncManager.requestSync()
        }
    }
}
