package com.tirupati.pos.core.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirupati.pos.core.sync.SyncManager
import com.tirupati.pos.core.sync.SyncStatusManager
import com.tirupati.pos.core.sync.SyncUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncCenterViewModel @Inject constructor(
    syncStatusManager: SyncStatusManager,
    private val syncManager: SyncManager
) : ViewModel() {

    val uiState: StateFlow<SyncUiState> = syncStatusManager.syncUiState

    fun requestSync() {
        viewModelScope.launch {
            syncManager.requestSync()
        }
    }
}
