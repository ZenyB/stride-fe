package com.trio.stride.data

import com.trio.stride.data.ble.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeartRateRepository @Inject constructor() {
    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate

    private val _distance = MutableStateFlow(0f)
    val distance: StateFlow<Float> = _distance

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Uninitialized)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    fun updateConnectionState(newState: ConnectionState) {
        _connectionState.value = newState
    }

    fun updateHeartRate(newRate: Int) {
        _heartRate.value = newRate
    }

    fun updateDistance(newDistance: Float) {
        _distance.value = newDistance
    }
}
