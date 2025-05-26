package com.trio.stride.ui.screens.record

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.ble.HeartRateReceiveManager
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.remote.dto.Coordinate
import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.data.repositoryimpl.GpsRepository
import com.trio.stride.data.repositoryimpl.RecordRepository
import com.trio.stride.data.service.RecordService
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.usecase.activity.CreateActivityUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val gpsRepository: GpsRepository,
    private val heartRateReceiveManager: HeartRateReceiveManager,
    private val sportManager: SportManager,
    private val createActivityUseCase: CreateActivityUseCase,
    private val savedStateHandle: SavedStateHandle
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

    val screenStatus: StateFlow<ScreenStatus> = recordRepository.screenStatus
    val recordStatus: StateFlow<RecordStatus> = recordRepository.recordStatus
    val gpsStatus: StateFlow<GPSStatus> = gpsRepository.gpsStatus

    val startPoint: StateFlow<Point?> = recordRepository.startPoint
    val mapView: StateFlow<MapView?> = recordRepository.mapView
    val locationPoints: StateFlow<List<Point>> = recordRepository.routePoints
    val coordinates: StateFlow<List<Coordinate>> = recordRepository.coordinates
    val mapViewportState: StateFlow<MapViewportState> = recordRepository.mapViewportState

    val categories: StateFlow<List<Category>> = sportManager.categories
    val sportsByCategory: StateFlow<Map<Category, List<Sport>>> = sportManager.sportsByCategory
    val currentSport: StateFlow<Sport?> = sportManager.currentSport

    val geometry: String? = savedStateHandle["geometry"]

    init {
        if (!geometry.equals("null")) {
            geometry?.let {
                val coords =
                    LineString.fromPolyline(it, 5).coordinates()

                recordRepository.updateRecommendRoute(coords)
            }
        }
    }

    override fun createInitialState(): RecordViewState = RecordViewState()

    private fun generateActivityName(sport: Sport): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..10 -> "Morning ${sport.name}"
            in 11..13 -> "Midday ${sport.name}"
            in 14..17 -> "Afternoon ${sport.name}"
            in 18..20 -> "Evening ${sport.name}"
            in 21..23 -> "Night ${sport.name}"
            else -> "Early morning ${sport.name}"
        }
    }

    fun saveActivity(
        createActivityRequestDto: CreateActivityRequestDTO,
        sport: Sport,
        context: Context,
        back: () -> Unit
    ) {
        val realHeartRates =
            if (heartRates.value.all { it == 0 }) emptyList<Int>() else heartRates.value
        var requestDto = createActivityRequestDto.copy(
            movingTimeSeconds = (time.value / 1000).toInt(),
            elapsedTimeSeconds = (elapsedTime.value / 1000).toInt(),
            coordinates = coordinates.value,
            heartRates = realHeartRates,
            sportId = sport.id
        )

        if (requestDto.name.isBlank()) {
            requestDto = requestDto.copy(name = generateActivityName(sport))
        }

        setState { currentState.copy(createActivityDto = requestDto, isSavingError = false) }

        Log.i("dto", requestDto.toString())

        viewModelScope.launch {
            createActivityUseCase.invoke(requestDto).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState {
                        currentState.copy(
                            isLoading = true,
                            isSavingError = false
                        )
                    }

                    is Resource.Success -> {
                        val startIntent = Intent(context, RecordService::class.java).apply {
                            action = RecordService.STOP_RECORDING
                        }
                        context.startService(startIntent)

                        Log.i("ACTIVITY_INFO_SAVED", requestDto.toString())
                        recordRepository.end()
                        back()
                    }

                    is Resource.Error -> {
                        setState { currentState.copy(isLoading = false, isSavingError = true) }
                    }
                }
            }
        }
    }

    fun saveAgain(context: Context) {
        viewModelScope.launch {
            createActivityUseCase.invoke(currentState.createActivityDto).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState {
                        currentState.copy(
                            isLoading = true,
                            isSavingError = false
                        )
                    }

                    is Resource.Success -> {
                        val startIntent = Intent(context, RecordService::class.java).apply {
                            action = RecordService.STOP_RECORDING
                        }
                        context.startService(startIntent)

                        Log.i("ACTIVITY_INFO_SAVED", currentState.createActivityDto.toString())
                        recordRepository.end()
                        setState { currentState.copy(isLoading = false, isSavingError = true) }
                    }

                    is Resource.Error -> {
                        setState { currentState.copy(isLoading = false, isSavingError = true) }
                    }
                }
            }
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

        setState { currentState.copy(isNotEnoughDataToSave = false) }

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
        if (currentSport.value?.sportMapType != null && coordinates.value.size < 2) {
            stop(context)
            setState { currentState.copy(isNotEnoughDataToSave = true) }
            return
        }

        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.PAUSE_RECORDING
        }
        context.startService(startIntent)

        recordRepository.finish()
    }

    fun discard(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.STOP_RECORDING
        }
        context.startService(startIntent)

        setState { currentState.copy(isNotEnoughDataToSave = false) }

        recordRepository.end()
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

    fun resetSaveActivityError() {
        setState { currentState.copy(isSavingError = false) }
    }

    fun setIsNotEnoughDataToSave(value: Boolean) {
        setState { currentState.copy(isNotEnoughDataToSave = value) }
    }

    data class RecordViewState(
        val isLoading: Boolean = false,
        val isNotEnoughDataToSave: Boolean = false,
        val isSavingError: Boolean = false,
        val bluetoothErrMessage: String? = null,
        val createActivityDto: CreateActivityRequestDTO = CreateActivityRequestDTO(),
    ) : IViewState

    enum class RecordStatus { NONE, RECORDING, FINISH, STOP }
    enum class GPSStatus { NO_GPS, ACQUIRING_GPS, GPS_READY }
    enum class ScreenStatus { DEFAULT, DETAIL, SENSOR, SAVING, SAVED }
}