package com.trio.stride.ui.screens.maps.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.remote.dto.RecommendRouteRequest
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.domain.usecase.route.GetRecommendedRouteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewMapViewModel @Inject constructor(
    private val getRecommendedRouteUseCase: GetRecommendedRouteUseCase,
    private val sportManager: SportManager,
) : BaseViewModel<ViewMapState>() {
    var currentDetailIndex by mutableIntStateOf(-1)

    private val _routeItems = MutableStateFlow<List<RouteItem>>(emptyList())
    val routeItems: StateFlow<List<RouteItem>> = _routeItems.asStateFlow()

    private val _mapView = MutableStateFlow<MapView?>(null)
    val mapView: StateFlow<MapView?> = _mapView

    private val _allRoutes = mutableStateMapOf<String, List<Point>>()

    private val _drawnRoutes = mutableMapOf<String, PolylineAnnotation?>()

    fun setMapView(newValue: MapView?) {
        _mapView.value = newValue
    }

    fun onRouteItemClick(index: Int) {
        currentDetailIndex = index
        setState { ViewMapState.ViewRouteDetail }
    }

    fun backToNormalView() {
        currentDetailIndex = -1
        setState { ViewMapState.Idle }
    }

    fun getDrawnRoute(index: Int): PolylineAnnotation? {
        return _drawnRoutes[index.toString()]
    }

    fun setDrawnRoute(index: Int, annotation: PolylineAnnotation?) {
        _drawnRoutes[index.toString()] = annotation
    }

    fun addRoute(key: String, points: List<Point>) {
        _allRoutes[key] = points
    }

    fun getRecommendRoute(selectedPoint: Point?, selectedSport: Sport) {
        Log.d("okhttp", "Getting routes")
        setState { ViewMapState.Loading }

        viewModelScope.launch {
            _routeItems.value = emptyList()
            _drawnRoutes.clear()
            val result =
                getRecommendedRouteUseCase(
                    request =
                    RecommendRouteRequest(
                        sportId = selectedSport.id,
                        latitude = selectedPoint?.latitude() ?: 10.873953237840828,
                        longitude = selectedPoint?.longitude() ?: 106.74647540531987,
                        limit = 5,
                        sportMapType = SportMapType.CYCLING
                    )
                )
            result
                .onSuccess { data ->
                    setState { ViewMapState.Idle }
                    _routeItems.value = data
                }
                .onFailure {
                    setState { ViewMapState.GetRouteError(it.message ?: "An error occurred") }
                }
        }
    }

    fun setCurrentSport(sport: Sport?) {
        sport?.let { sportManager.updateCurrentSport(sport) }
    }

    fun savingRoute() {
        setState { ViewMapState.SavingRoute }
    }

    fun discardSaving() {
        setState { ViewMapState.Idle }
    }

    override fun createInitialState(): ViewMapState {
        return ViewMapState.Idle
    }
}