package com.tirupati.pos.core.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<S : BaseUiState, E : BaseUiEvent, F : BaseUiEffect>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffect = Channel<F>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    protected fun updateState(reducer: S.() -> S) {
        _uiState.value = _uiState.value.reducer()
    }

    protected suspend fun emitEffect(effect: F) {
        _uiEffect.send(effect)
    }

    abstract fun onEvent(event: E)
}
