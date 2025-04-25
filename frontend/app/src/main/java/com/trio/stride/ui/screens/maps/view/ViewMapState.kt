package com.trio.stride.ui.screens.maps.view

import com.trio.stride.domain.viewstate.IViewState


sealed class ViewMapState : IViewState {
    object Idle : ViewMapState()
    object Loading : ViewMapState()
    object ViewRouteDetail: ViewMapState()
    data class GetRouteError(val message: String) : ViewMapState()


}