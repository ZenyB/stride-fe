package com.trio.stride.data.ble

import android.bluetooth.BluetoothDevice
import com.trio.stride.ui.utils.ble.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

interface HeartRateReceiveManager {
    val data: MutableSharedFlow<Resource<HeartRateResult>>

    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val selectedDevice: StateFlow<BluetoothDevice?>
    val isBluetoothOn: StateFlow<Boolean>

    fun setBluetoothState(isOn: Boolean)
    fun connectToDevice(device: BluetoothDevice)

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

}