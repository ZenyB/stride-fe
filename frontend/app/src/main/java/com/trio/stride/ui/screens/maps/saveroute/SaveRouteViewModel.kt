package com.trio.stride.ui.screens.maps.saveroute

import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.usecase.activity.SaveRouteFromActivityUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveRouteViewModel @Inject constructor(
    private val saveRouteFromActivityUseCase: SaveRouteFromActivityUseCase
) : BaseViewModel<SaveRouteState>() {
    fun saveRoute(routeId: String) {
        setState { SaveRouteState.IsSaving }
        viewModelScope.launch {
            val result =
                saveRouteFromActivityUseCase(routeId)
            result
                .onSuccess { data ->
                    setState { SaveRouteState.Success }
                }
                .onFailure {
                    setState {
                        SaveRouteState.ErrorSaving(it.message ?: "An error occurred")
                    }
                }
        }
    }

    fun resetState() {
        setState { SaveRouteState.Idle }
    }

    override fun createInitialState(): SaveRouteState {
        return SaveRouteState.Idle
    }
}


sealed class SaveRouteState : IViewState {
    data object Idle : SaveRouteState()
    data object IsSaving : SaveRouteState()
    data class ErrorSaving(val message: String) : SaveRouteState()
    data object Success : SaveRouteState()
}