package com.trio.stride.ui.screens.maps.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.dto.RecommendRouteRequest
import com.trio.stride.domain.model.RouteItem
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
) : BaseViewModel<ViewMapState>() {
    var currentDetailIndex by mutableIntStateOf(-1)

    private val _routeItems = MutableStateFlow<List<RouteItem>>(emptyList())
    val routeItems: StateFlow<List<RouteItem>> = _routeItems.asStateFlow()

    private val _allRoutes = mutableStateMapOf<String, List<Point>>()

    private val _drawnRoutes = mutableMapOf<String, PolylineAnnotation?>()


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

    fun getRecommendRoute() {
        Log.d("okhttp", "Getting routes")
        setState { ViewMapState.Loading }

        viewModelScope.launch {
            val result =
                getRecommendedRouteUseCase(
                    request =
                    RecommendRouteRequest(
                        sportId = "9e0e6469-4a9c-4d18-969d-0cdf600288b3",
                        latitude = 10.873953237840828,
                        longitude = 106.74647540531987,
                        around = 1000,
                        limit = 5
                    )
                )
            result
                .onSuccess { data ->
                    setState { ViewMapState.Idle }
                    data.forEachIndexed { index, item ->
                        _routeItems.value += item
                    }
                }
                .onFailure {
                    setState { ViewMapState.GetRouteError(it.message ?: "An error occurred") }
                }
        }
    }

    override fun createInitialState(): ViewMapState {
        return ViewMapState.Idle
    }
}