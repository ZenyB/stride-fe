package com.trio.stride.ui.screens.record

import android.content.Context
import android.content.Intent
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.trio.stride.RecordService
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.RecordRepository
import com.trio.stride.domain.model.ActivityMetric
import com.trio.stride.domain.model.Coordinate
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : BaseViewModel<RecordViewModel.RecordViewState>() {

    val distance: StateFlow<Double> = recordRepository.distance
    val avgSpeed: StateFlow<Double> = recordRepository.avgSpeed
    val time: StateFlow<Long> = recordRepository.time
    val activityType: StateFlow<ActivityType> = recordRepository.activityType
    val screenStatus: StateFlow<ScreenStatus> = recordRepository.screenStatus
    val recordStatus: StateFlow<RecordStatus> = recordRepository.recordStatus
    val startPoint: StateFlow<Point?> = recordRepository.startPoint
    val mapView: StateFlow<MapView?> = recordRepository.mapView
    val locationPoints: StateFlow<List<Point>> = recordRepository.routePoints
    val coordinates: StateFlow<List<Coordinate>> = recordRepository.coordinates
    val mapViewportState: MapViewportState = recordRepository.mapViewportState

    override fun createInitialState(): RecordViewState = RecordViewState()

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
        recordRepository.updateMapView(mapView)
    }

    fun enableUserLocation() {
        recordRepository.enableUserLocation()
    }

    fun reloadMapStyle() {
        recordRepository.reloadMapStyle()
    }

    fun startRecord(startPoint: Point, context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.START_RECORDING
        }
        context.startService(startIntent)

        recordRepository.startRecord(startPoint = startPoint)
    }

    fun resume(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.RESUME_RECORDING
        }
        context.startService(startIntent)

        recordRepository.resume()
    }

    fun stop(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.PAUSE_RECORDING
        }
        context.startService(startIntent)

        recordRepository.stop()
    }

    fun finish(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.STOP_RECORDING
        }
        context.startService(startIntent)

        recordRepository.finish()
    }

    fun handleVisibleMetric() {
        if (screenStatus.value == ScreenStatus.DEFAULT)
            recordRepository.updateScreenStatus(ScreenStatus.DETAIL)
        else
            recordRepository.updateScreenStatus(ScreenStatus.DEFAULT)
    }

    data class RecordViewState(
        val isLoading: Boolean = false,
        val activityMetric: ActivityMetric = ActivityMetric()
    ) : IViewState

    enum class RecordStatus { NONE, RECORDING, FINISH, STOP }
    enum class GPSStatus { NO_GPS, ACQUIRING_GPS, GPS_READY }
    enum class ScreenStatus { DEFAULT, DETAIL, SENSOR, SAVING }
    enum class ActivityType { RUN, CLIMB, RIDE }
}