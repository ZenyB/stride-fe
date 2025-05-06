package com.trio.stride.ui.screens.record

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.ble.HeartRateReceiveManager
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.dto.Coordinate
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.repositoryimpl.GpsRepository
import com.trio.stride.data.repositoryimpl.RecordRepository
import com.trio.stride.data.service.RecordService
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.usecase.activity.CreateActivityUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val gpsRepository: GpsRepository,
    private val heartRateReceiveManager: HeartRateReceiveManager,
    private val sportManager: SportManager,
    private val createActivityUseCase: CreateActivityUseCase,
) : BaseViewModel<RecordViewModel.RecordViewState>() {

    val distance: StateFlow<Double> = recordRepository.distance
    val avgSpeed: StateFlow<Double> = recordRepository.avgSpeed
    val time: StateFlow<Long> = recordRepository.time
    val elapsedTime: StateFlow<Long> = recordRepository.elapsedTime
    val heartRates: StateFlow<List<Int>> = recordRepository.heartRates

    val heartRate: StateFlow<Int> = recordRepository.heartRate
    var connectionState = recordRepository.connectionState
    val scannedDevices = heartRateReceiveManager.scannedDevices
    val isBluetoothOn: StateFlow<Boolean> = heartRateReceiveManager.isBluetoothOn
    val selectedDevice: StateFlow<BluetoothDevice?> = heartRateReceiveManager.selectedDevice

    val activityType: StateFlow<ActivityType> = recordRepository.activityType
    val screenStatus: StateFlow<ScreenStatus> = recordRepository.screenStatus
    val recordStatus: StateFlow<RecordStatus> = recordRepository.recordStatus
    val gpsStatus: StateFlow<GPSStatus> = gpsRepository.gpsStatus

    val startPoint: StateFlow<Point?> = recordRepository.startPoint
    val mapView: StateFlow<MapView?> = recordRepository.mapView
    val locationPoints: StateFlow<List<Point>> = recordRepository.routePoints
    val coordinates: StateFlow<List<Coordinate>> = recordRepository.coordinates
    val mapViewportState: MapViewportState = recordRepository.mapViewportState

    val currentSport: StateFlow<Sport> = sportManager.currentSport

    override fun createInitialState(): RecordViewState = RecordViewState()

    private fun createActivity(activityRequestDto: CreateActivityRequestDTO) {
        viewModelScope.launch {
            createActivityUseCase.invoke(activityRequestDto).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState {
                        currentState.copy(
                            isLoading = true,
                            isSavingError = false
                        )
                    }

                    is Resource.Success -> {
                        setState { currentState.copy(isLoading = false, isSavingError = false) }
                        recordRepository.saved()
                    }

                    is Resource.Error -> {
                        setState { currentState.copy(isLoading = false, isSavingError = true) }
                    }
                }
            }
        }
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
        recordRepository.updateMapView(mapView)
    }

    fun setBluetoothState(isOn: Boolean) {
        heartRateReceiveManager.setBluetoothState(isOn)
    }

    fun enableUserLocation() {
        recordRepository.enableUserLocation()
    }

    fun reloadMapStyle() {
        recordRepository.reloadMapStyle()
    }

    fun connectToDevice(context: Context, device: BluetoothDevice) {
        val intent = Intent(context, RecordService::class.java).apply {
            action = RecordService.CONNECT_TO_DEVICE
            putExtra(BluetoothDevice.EXTRA_DEVICE, device)
        }
        context.startService(intent)
    }

    fun reconnect(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.RECONNECT
        }
        context.startService(startIntent)
    }


    fun disconnect(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.DISCONNECT
        }
        context.startService(startIntent)
    }

    fun initializeConnection(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.START_RECEIVING
        }
        context.startService(startIntent)
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
            action = RecordService.PAUSE_RECORDING
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

    fun handleShowSensorView() {
        recordRepository.updateScreenStatus(ScreenStatus.SENSOR)
    }

    fun handleShowSaveActivityView() {
        recordRepository.updateScreenStatus(ScreenStatus.SAVING)
    }

    fun handleBackToDefault() {
        recordRepository.updateScreenStatus(ScreenStatus.DEFAULT)
    }

    fun handleDismissSaveActivity(context: Context) {
        recordRepository.updateScreenStatus(ScreenStatus.DEFAULT)
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.RESUME_RECORDING
        }
        context.startService(startIntent)

        recordRepository.resume()
    }

    fun updateGpsStatus(status: GPSStatus) {
        gpsRepository.updateGpsStatus(status)
    }

    fun updateCurrentSport(sport: Sport) {
        sportManager.updateCurrentSport(sport)
    }

    fun saveActivity(createActivityRequestDto: CreateActivityRequestDTO, context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.STOP_RECORDING
        }
        context.startService(startIntent)

        val requestDto = createActivityRequestDto.copy(
            totalDistance = distance.value,
            movingTimeSeconds = (time.value / 1000).toInt(),
            elapsedTimeSeconds = (elapsedTime.value / 1000).toInt(),
            avgSpeed = avgSpeed.value,
            coordinates = coordinates.value,
            heartRates = heartRates.value,
        )
        Log.i("ACTIVITY_INFO_SAVED", requestDto.toString())
        createActivity(requestDto)
    }

    data class RecordViewState(
        val isLoading: Boolean = false,
        val isSavingError: Boolean = false,
        val bluetoothErrMessage: String? = null,
        val sport: Sport = Sport(), //save sport trong manager?
    ) : IViewState

    enum class RecordStatus { NONE, RECORDING, FINISH, STOP }
    enum class GPSStatus { NO_GPS, ACQUIRING_GPS, GPS_READY }
    enum class ScreenStatus { DEFAULT, DETAIL, SENSOR, SAVING, SAVED }
    enum class ActivityType { RUN, CLIMB, RIDE }
}