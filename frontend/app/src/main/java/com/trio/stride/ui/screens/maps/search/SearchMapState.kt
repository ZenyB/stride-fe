package com.trio.stride.ui.screens.maps.search

import com.mapbox.geojson.Point
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.screens.signup.SignUpViewState

sealed class SearchMapState : IViewState {
    object Idle : SearchMapState()
    object Loading : SearchMapState()
    data class Success(val point: Point) : SearchMapState()
    data class Error(val message: String) : SearchMapState()
}
