package com.trio.stride.data.repositoryimpl

import android.content.Context
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.util.isEmpty
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.data.remote.dto.Coordinate
import com.trio.stride.ui.screens.maps.view.ZOOM
import com.trio.stride.ui.screens.record.RecordViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class RecordRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val MIN_METER_TO_ADD_NEW_POINT = 1

    private val _sportName = MutableStateFlow("")
    val sportName: StateFlow<String> = _sportName

    private val _recording = MutableStateFlow(false)
    val recording: StateFlow<Boolean> = _recording

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate

    private val _heartRates = MutableStateFlow(emptyList<Int>())
    val heartRates: StateFlow<List<Int>> = _heartRates

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance

    private val _time = MutableStateFlow(0L)
    val time: StateFlow<Long> = _time


    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private val _avgSpeed = MutableStateFlow(0.0)
    val avgSpeed: StateFlow<Double> = _avgSpeed

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Uninitialized)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _screenStatus = MutableStateFlow(RecordViewModel.ScreenStatus.DEFAULT)
    val screenStatus: StateFlow<RecordViewModel.ScreenStatus> = _screenStatus

    private val _recordStatus = MutableStateFlow(RecordViewModel.RecordStatus.NONE)
    val recordStatus: StateFlow<RecordViewModel.RecordStatus> = _recordStatus

    private val _startPoint = MutableStateFlow<Point?>(null)
    val startPoint: StateFlow<Point?> = _startPoint

    private val _routePoints = MutableStateFlow(emptyList<Point>())
    val routePoints: StateFlow<List<Point>> = _routePoints

    private val _recommendRoutePoints = MutableStateFlow(emptyList<Point>())
    val recommendRoutePoints: StateFlow<List<Point>> = _recommendRoutePoints

    private val _coordinates = MutableStateFlow(emptyList<Coordinate>())
    val coordinates: StateFlow<List<Coordinate>> = _coordinates

    private val _mapView = MutableStateFlow<MapView?>(null)
    val mapView: StateFlow<MapView?> = _mapView

    private val _mapViewportState = MutableStateFlow(MapViewportState().apply {
        setCameraOptions {
            center(Point.fromLngLat(106.80259579, 10.87007182)) //UIT
            zoom(ZOOM)
            pitch(0.0)
        }
    })
    val mapViewportState: StateFlow<MapViewportState> = _mapViewportState

    fun updateSportName(newSportName: String) {
        _sportName.value = newSportName
    }

    fun updateConnectionState(newState: ConnectionState) {
        _connectionState.value = newState
    }

    fun updateHeartRate(newRate: Int) {
        _heartRate.value = newRate
    }

    fun updateDistance(newDistance: Double) {
        _distance.value = newDistance
    }

    fun updateTime(newTime: Long) {
        _time.value = newTime
    }

    fun updateElapsedTime(newTime: Long) {
        _elapsedTime.value = newTime
    }

    fun updateAvgSpeed(newSpeed: Double) {
        _avgSpeed.value = newSpeed
    }

    fun updateScreenStatus(newValue: RecordViewModel.ScreenStatus) {
        _screenStatus.value = newValue
    }

    fun updateRecordStatus(newValue: RecordViewModel.RecordStatus) {
        _recordStatus.value = newValue
    }

    fun updateMapView(newValue: MapView?) {
        _mapView.value = newValue
    }

    fun updateStartPoint(newValue: Point) {
        _startPoint.value = newValue
    }

    fun updateRoutePoints(newValue: List<Point>) {
        _routePoints.value = newValue
    }

    fun updateCoordinates(newValue: List<Coordinate>) {
        _coordinates.value = newValue
    }

    fun updateRecommendRoute(newValue: List<Point>) {
        _recommendRoutePoints.value = newValue
        drawRecommendRoute()
    }

    fun startRecord(startPoint: Point) {
        _recording.value = true
        _startPoint.value = startPoint
        _recordStatus.value = RecordViewModel.RecordStatus.RECORDING
        addPoints(startPoint)
    }

    fun resume() {
        _recordStatus.value = RecordViewModel.RecordStatus.RECORDING
        if (screenStatus.value == RecordViewModel.ScreenStatus.SAVING)
            _screenStatus.value = RecordViewModel.ScreenStatus.DEFAULT
        _recording.value = true
    }

    fun stop() {
        _recordStatus.value = RecordViewModel.RecordStatus.STOP
        _recording.value = false
    }

    fun finish() {
        _screenStatus.value = RecordViewModel.ScreenStatus.SAVING
        _recordStatus.value = RecordViewModel.RecordStatus.STOP
        _recording.value = false
    }

    fun end() {
        _screenStatus.value = RecordViewModel.ScreenStatus.DEFAULT
        _recordStatus.value = RecordViewModel.RecordStatus.NONE
        _coordinates.value = emptyList()
        _time.value = 0
        _distance.value = 0.0
        _avgSpeed.value = 0.0
        _elapsedTime.value = 0
        _startPoint.value = null
        _routePoints.value = emptyList()
        _recommendRoutePoints.value = emptyList()
        _heartRates.value = emptyList()
        mapView.value?.let { updatePolyline() }
        _recording.value = false
    }

    private fun drawRecommendRoute() {
        val mapboxMap = mapView.value?.mapboxMap
        val lineString = LineString.fromLngLats(recommendRoutePoints.value)

        mapboxMap?.getStyle { style ->
            val sourceId = "recommend-route-source"
            val layerId = "recommend-route-layer"

            val source = style.getSourceAs<GeoJsonSource>(sourceId)
            if (source != null) {
                source.geometry(lineString)
            } else {
                val newSource = geoJsonSource(sourceId) {
                    geometry(lineString)
                }
                style.addSource(newSource)

                val layer = lineLayer(layerId, sourceId) {
                    lineColor("#D10A46")
                    lineWidth(4.0)
                }
                style.addLayer(layer)
            }
        }

        if (recommendRoutePoints.value.isNotEmpty()) {
            val options = CameraOptions.Builder().center(recommendRoutePoints.value[0]).build()
            mapView.value?.let { safeMapView ->
                safeMapView.mapboxMap.cameraForCoordinates(
                    recommendRoutePoints.value,
                    options, null, ZOOM, null
                ) { result ->
                    if (result.isEmpty) {
                        //TODO: error
                    } else {
                        mapViewportState.value.flyTo(result)
                    }
                }
            }
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    private fun updatePolyline() {
        if (routePoints.value.size < 2) return
        val lineString = LineString.fromLngLats(routePoints.value)

        val map = mapView.value?.mapboxMap

        map?.getStyle { style ->
            val sourceId = "live-route-source"
            val layerId = "live-route-layer"

            val source = style.getSourceAs<GeoJsonSource>(sourceId)
            if (source != null) {
                source.geometry(lineString)
            } else {
                val newSource = geoJsonSource(sourceId) {
                    geometry(lineString)
                }
                style.addSource(newSource)

                style.addLayerAbove(
                    lineLayer(layerId, sourceId) {
                        lineColor("#2571DB")
                        lineWidth(4.0)
                    },
                    "recommend-route-layer"
                )
            }
        }
    }

    private fun shouldAddPoint(newPoint: Point): Boolean {
        val points = routePoints.value
        if (points.isEmpty()) return true

        val lastPoint = points.last()
        val distance = haversineDistance(
            lastPoint.latitude(),
            lastPoint.longitude(),
            newPoint.latitude(),
            newPoint.longitude()
        )

        return distance >= MIN_METER_TO_ADD_NEW_POINT
    }

    fun addPoints(point: Point) {
        if (shouldAddPoint(point) && mapView.value != null && recordStatus.value == RecordViewModel.RecordStatus.RECORDING) {
            val newRoutePoints = routePoints.value.toMutableList()
            newRoutePoints.add(point)

            val newCoordinates = coordinates.value.toMutableList()
            newCoordinates.add(
                Coordinate(
                    coordinate = listOf(point.longitude(), point.latitude()),
                    timestamp = System.currentTimeMillis()
                )
            )

            val newHeartRates = heartRates.value.toMutableList()
            newHeartRates.add(heartRate.value)

            var newDistance = distance.value
            routePoints.value.lastOrNull()?.let { lastPoint ->
                val distanceToLastPoint = haversineDistance(
                    lastPoint.latitude(),
                    lastPoint.longitude(),
                    point.latitude(),
                    point.longitude()
                )
                newDistance += distanceToLastPoint
            }

            _distance.value = newDistance
            _routePoints.value = newRoutePoints
            _coordinates.value = newCoordinates
            _heartRates.value = newHeartRates

            updatePolyline()
        }
    }

    fun enableUserLocation() {
        mapView.value?.location?.updateSettings {
            enabled = true
            locationPuck = createDefault2DPuck(withBearing = true)
            puckBearingEnabled = true
            puckBearing = PuckBearing.HEADING
        }
    }

    fun reloadMapStyle() {
        drawRecommendRoute()
        updatePolyline()
    }
}
