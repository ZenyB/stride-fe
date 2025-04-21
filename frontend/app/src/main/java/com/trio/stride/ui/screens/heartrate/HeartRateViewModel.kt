package com.trio.stride.ui.screens.heartrate

import com.trio.stride.data.HeartRateRepository
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.stride.RecordService
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.data.ble.HeartRateReceiveManager
import com.trio.stride.ui.utils.ble.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeartRateViewModel @Inject constructor(
    private val heartRateReceiveManager: HeartRateReceiveManager,
    heartRateRepo: HeartRateRepository
) : ViewModel() {
    var errorMessge by mutableStateOf<String?>(null)
        private set


    val heartRate: StateFlow<Int> = heartRateRepo.heartRate


    var connectionState = heartRateRepo.connectionState

    val scannedDevices = heartRateReceiveManager.scannedDevices

    val isBluetoothOn: StateFlow<Boolean> = heartRateReceiveManager.isBluetoothOn
    val selectedDeviceAddress: StateFlow<String> = heartRateReceiveManager.selectedDeviceAddress


    fun setBluetoothState(isOn: Boolean) {
        heartRateReceiveManager.setBluetoothState(isOn)
    }

    fun startWorkout(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.START_RECORDING
        }
        context.startService(startIntent)
    }

    fun stopWorkout(context: Context) {
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.STOP_RECORDING
        }
        context.startService(startIntent)
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
        errorMessge = null
        val startIntent = Intent(context, RecordService::class.java).apply {
            action = RecordService.START_RECEIVING
        }
        context.startService(startIntent)
    }

}