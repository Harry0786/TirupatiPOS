package com.tirupati.pos.core.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<E : BaseUiEvent, F : BaseUiEffect> : ViewModel() {

    private val _effect = MutableSharedFlow<F>(extraBufferCapacity = 1)
    val effect: SharedFlow<F> = _effect.asSharedFlow()

    protected fun sendEffect(effect: F) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    abstract fun onEvent(event: E)
}
