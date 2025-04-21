package com.trio.stride.ui.screens.record

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.model.ActivityMetric
import com.trio.stride.domain.model.Coordinate
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class RecordViewModel @Inject constructor(

) : BaseViewModel<RecordViewModel.RecordViewState>() {

    val MIN_METER_TO_ADD_NEW_POINT = 1
    private var recordingJob: Job? = null
    private var accumulatedTime = 0L // thời gian đã ghi nhận trước khi pause
    private var lastStartTime = 0L // thời điểm resume

    override fun createInitialState(): RecordViewState = RecordViewState()

    init {

    }

    private fun updatePolyline(mapView: MapView, points: List<Point>) {
        if (points.size < 2) return
        val lineString = LineString.fromLngLats(points)

        val map = mapView.mapboxMap


        map.getStyle { style ->
            val sourceId = "live-route-source"
            val layerId = "live-route-layer"

            // Update existing source or create a new one
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
                        lineColor("#2571db") // Blue line
                        lineWidth(4.0)
                    },
                    "default-route-layer"
                )
            }
        }
    }

    private fun shouldAddPoint(newPoint: Point): Boolean {
        var points = currentState.locationPoints
        if (points.isEmpty()) return true

        val lastPoint = points.last()
        val distance = haversineDistance(
            lastPoint.latitude(),
            lastPoint.longitude(),
            newPoint.latitude(),
            newPoint.longitude()
        ) // Calculate distance in meters

        return distance >= MIN_METER_TO_ADD_NEW_POINT
    }

    private fun startRecordJob() {
        lastStartTime = System.currentTimeMillis()

        recordingJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)

                val now = System.currentTimeMillis()
                val elapsedTime = accumulatedTime + (now - lastStartTime)

                val avgSpeed = if (elapsedTime > 0) {
                    (currentState.activityMetric.distance / (elapsedTime / 1000.0)).toFloat()
                } else 0f

                setState {
                    currentState.copy(
                        activityMetric = currentState.activityMetric.copy(
                            time = elapsedTime,
                            avgSpeed = avgSpeed,
                        )
                    )
                }
            }
        }
    }

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // Distance in meters
    }

    fun drawRoute(mapView: MapView, routeCoordinates: List<Point>) {
        val mapboxMap = mapView.mapboxMap
        val lineString = LineString.fromLngLats(routeCoordinates)

        mapboxMap.getStyle { style ->
            val sourceId = "default-route-source"
            val layerId = "default-route-layer"
            val source = geoJsonSource(sourceId) {
                geometry(lineString)
            }
            style.addSource(source)

            val layer = lineLayer(layerId, sourceId) {
                lineColor("#e01659")
                lineWidth(4.0)
            }
            style.addLayer(layer)
        }
    }

    fun setMapView(mapView: MapView?) {
        setState { currentState.copy(mapView = mapView) }
    }

    fun startRecord(startPoint: Point) {
        startRecordJob()

        setState {
            currentState.copy(
                recordStatus = RecordStatus.RECORDING,
                startPoint = startPoint
            )
        }
        addPoints(startPoint)
    }

    fun resume() {
        setState { currentState.copy(recordStatus = RecordStatus.RECORDING) }

        startRecordJob()
    }

    fun stop() {
        accumulatedTime += System.currentTimeMillis() - lastStartTime
        recordingJob?.cancel()
        setState {
            currentState.copy(recordStatus = RecordStatus.STOP)
        }
    }

    fun finish() {
        Log.i("FINISH_RUN", currentState.coordinates.toString())
        recordingJob?.cancel()
        recordingJob = null
        accumulatedTime = 0L
        setState {
            currentState.copy(
                screenStatus = ScreenStatus.SAVING,
                recordStatus = RecordStatus.FINISH,
                locationPoints = emptyList(),
                coordinates = emptyList(),
                activityMetric = ActivityMetric()
            )
        }
    }

    fun addPoints(point: Point) {
        if (shouldAddPoint(point) && currentState.mapView != null && currentState.recordStatus == RecordStatus.RECORDING) {
            var temp = currentState.locationPoints.toMutableList()
            temp.add(point)

            var tempCoordinates = currentState.coordinates.toMutableList()
            tempCoordinates.add(
                Coordinate(
                    coordinate = listOf(point.longitude(), point.latitude()),
                    timeStamp = System.currentTimeMillis()
                )
            )

            var newDistance = currentState.activityMetric.distance
            currentState.locationPoints.lastOrNull()?.let { lastPoint ->
                val distanceToLastPoint = haversineDistance(
                    lastPoint.latitude(),
                    lastPoint.longitude(),
                    point.latitude(),
                    point.longitude()
                )
                newDistance += distanceToLastPoint
            }

            setState {
                currentState.copy(
                    locationPoints = temp,
                    coordinates = tempCoordinates,
                    activityMetric = currentState.activityMetric.copy(distance = newDistance)
                )
            }

            updatePolyline(currentState.mapView!!, currentState.locationPoints)
        }
    }

    fun handleVisibleMetric() {
        if (currentState.screenStatus == ScreenStatus.DEFAULT)
            setState { currentState.copy(screenStatus = ScreenStatus.DETAIL) }
        else
            setState { currentState.copy(screenStatus = ScreenStatus.DEFAULT) }
    }

    data class RecordViewState(
        val isLoading: Boolean = false,
        val activityType: ActivityType = ActivityType.RUN,
        val screenStatus: ScreenStatus = ScreenStatus.DEFAULT,
        val recordStatus: RecordStatus = RecordStatus.NONE,
        val startPoint: Point? = null,
        val mapView: MapView? = null,
        val locationPoints: List<Point> = emptyList(),
        val coordinates: List<Coordinate> = emptyList(),
        val activityMetric: ActivityMetric = ActivityMetric()
    ) : IViewState

    enum class RecordStatus { NONE, RECORDING, FINISH, STOP }
    enum class GPSStatus { NO_GPS, ACQUIRING_GPS, GPS_READY }
    enum class ScreenStatus { DEFAULT, DETAIL, SENSOR, SAVING }
    enum class ActivityType { RUN, CLIMB, RIDE }
}