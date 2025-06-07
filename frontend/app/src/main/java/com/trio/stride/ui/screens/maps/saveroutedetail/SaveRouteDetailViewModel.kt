package com.trio.stride.ui.screens.maps.saveroutedetail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.MapView
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.domain.usecase.route.DeleteUserRouteUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveRouteDetailViewModel @Inject constructor(
    private val sportManager: SportManager,
    private val deleteUserRouteUseCase: DeleteUserRouteUseCase
) : BaseViewModel<ViewRouteDetailState>() {

    private val _mapView = MutableStateFlow<MapView?>(null)
    val mapView: StateFlow<MapView?> = _mapView

    fun deleteSavedRoute(id: String) {
        setState { ViewRouteDetailState.Loading }
        Log.d("okhttp", "Delete user route")
        viewModelScope.launch {
            val result = deleteUserRouteUseCase(id)

            result
                .onSuccess { data ->
                    setState { ViewRouteDetailState.Success }
                    Log.d("okhttp", "Delete success")
                }
                .onFailure {
                    setState { ViewRouteDetailState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    fun setCurrentSport(sportId: String) {
        val sport = sportManager.sportsWithMap.value.firstOrNull { it.id == sportId }
        sport?.let { sportManager.updateCurrentSport(sport) }
    }

    fun resetState(){
        setState { ViewRouteDetailState.Idle }
    }
    override fun createInitialState(): ViewRouteDetailState {
        return ViewRouteDetailState.Idle
    }

}

sealed class ViewRouteDetailState : IViewState {
    object Idle : ViewRouteDetailState()
    object Loading : ViewRouteDetailState()
    object Success : ViewRouteDetailState()
    data class Error(val message: String) : ViewRouteDetailState()

}