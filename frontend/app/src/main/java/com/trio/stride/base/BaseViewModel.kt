package com.trio.stride.base

import androidx.lifecycle.ViewModel
import com.trio.stride.domain.viewstate.IViewEvent
import com.trio.stride.domain.viewstate.IViewState
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<State : IViewState> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    val currentState: State get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }
}