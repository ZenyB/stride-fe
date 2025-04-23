package com.trio.stride.ui.screens.maps.view

import com.mapbox.geojson.Point
import com.trio.stride.domain.viewstate.IViewState


sealed class ViewMapState : IViewState {
    object Idle : ViewMapState()
    object Loading : ViewMapState()
}