package com.trio.stride.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.trio.stride.ui.utils.ble.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class HeartRateBLEReceiverManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : HeartRateReceiveManager {
    private val DEVICE_NAME = "Galaxy Fit3 (DD5D)"
    override val data: MutableSharedFlow<Resource<HeartRateResult>> = MutableSharedFlow()

    private val _isBluetoothOn = MutableStateFlow(bluetoothAdapter.isEnabled)
    override val isBluetoothOn: StateFlow<Boolean> = _isBluetoothOn

    private val _selectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    override val selectedDevice: StateFlow<BluetoothDevice?> = _selectedDevice

    private val bleScanner: BluetoothLeScanner?
        get() = bluetoothAdapter.bluetoothLeScanner

    override val scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    private val foundAddresses = mutableSetOf<String>()


    private val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
    private val HEART_RATE_CHARACTERISTIC_UUID =
        UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")
    private val CCCD_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"

    val scanFilter = ScanFilter.Builder()
        .setServiceUuid(ParcelUuid(HEART_RATE_SERVICE_UUID))
        .build()

    val filters = listOf(scanFilter)

    private val scanSettings =
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

    private var gatt: BluetoothGatt? = null

    private var isScanning = false

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d("bluetoothScan", "device name ${result?.device?.name}")
            result?.device?.let { device ->
                if (!device.name.isNullOrEmpty() && foundAddresses.add(
                        device.address
                    )
                ) {
//                    isScanning = false
//                    bleScanner?.stopScan(this)
                    scannedDevices.value += device
                }
            }
        }
    }

    override fun connectToDevice(device: BluetoothDevice) {
        device.connectGatt(
            context,
            false,
            gattCallback,
            BluetoothDevice.TRANSPORT_LE
        )
        _selectedDevice.value = device

        bleScanner?.stopScan(scanCallback)
    }

    private var currentConnectionAttempt = 1
    private var MAXIMUM_CONNECTION_ATTEMPT = 5

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d("bluetoothScan", "Connection state changed: $newState (status: $status)")

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "Discovering Services..."))
                    }
                    gatt.discoverServices()
                    this@HeartRateBLEReceiverManager.gatt = gatt
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    coroutineScope.launch {
                        data.emit(
                            Resource.Success(
                                data = HeartRateResult(
                                    0,
                                    ConnectionState.Disconnected
                                )
                            )
                        )
                    }
                    _selectedDevice.value = null
                    gatt.close()
                }
            } else {
                gatt.close()
                currentConnectionAttempt += 1
                coroutineScope.launch {
                    data.emit(
                        Resource
                            .Loading(message = "Attempting to connect $currentConnectionAttempt/$MAXIMUM_CONNECTION_ATTEMPT")
                    )
                }

                if (currentConnectionAttempt <= MAXIMUM_CONNECTION_ATTEMPT) {
                    startReceiving()
                } else {
                    coroutineScope.launch {
                        data.emit(Resource.Error(errorMessage = "Could not connect to ble device"))
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d("bluetoothScan", "onServicesDiscovered: status=$status")

            with(gatt) {
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU space"))
                }

                gatt.requestMtu(512)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d("bluetoothScan", "mtu $mtu")

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("bluetoothScan", "✅ MTU request successful, new MTU: $mtu")
            } else {
                Log.e("bluetoothScan", "❌ MTU request failed with status: $status")
            }

            val characteristic =
                findCharacteristic(HEART_RATE_SERVICE_UUID, HEART_RATE_CHARACTERISTIC_UUID)
            if (characteristic == null) {
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not find heart rate publisher"))
                }
                return
            }

            enableNotification(characteristic)
        }


        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            Log.d("bluetoothScan", "❤️ on characteristic change")

            with(characteristic) {
                when (uuid) {
                    HEART_RATE_CHARACTERISTIC_UUID -> {
                        val heartRate = parseHeartRateMeasurement(characteristic.value)
                        val heartRateResult =
                            HeartRateResult(heartRate = heartRate, ConnectionState.Connected)
                        Log.d("bluetoothScan", "❤️ Heart Rate: $heartRate bpm")

                        coroutineScope.launch {
                            data.emit(
                                Resource.Success(data = heartRateResult)
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            Log.d(
                "bluetoothScan",
                "Characteristic read: ${characteristic.uuid} with value: ${characteristic.value?.joinToString()}"
            )

        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("bluetoothScan", "✅ CCCD descriptor write success (retry notification set)")

                val characteristic = descriptor.characteristic
                gatt.setCharacteristicNotification(characteristic, true)
            } else {
                Log.e("bluetoothScan", "❌ CCCD descriptor write failed with status $status")
            }
        }
    }

    override fun setBluetoothState(isOn: Boolean) {
        _isBluetoothOn.value = isOn
    }

    private fun parseHeartRateMeasurement(data: ByteArray): Int {
        if (data.isEmpty()) return -1

        val flags = data[0].toInt()
        val is16Bit = flags and 0x01 != 0

        return if (is16Bit && data.size >= 3) {
            ((data[2].toInt() and 0xFF) shl 8) or (data[1].toInt() and 0xFF)
        } else if (data.size >= 2) {
            data[1].toInt() and 0xFF
        } else {
            -1
        }
    }

    private fun enableNotification(characteristic: BluetoothGattCharacteristic) {
        Log.d("bluetoothScan", "enable notification")

        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val properties = characteristic.properties

        val payload = when {
            properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0 -> {
                Log.d("bluetoothScan", "Characteristic supports indicate")
                BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            }

            properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0 -> {
                Log.d("bluetoothScan", "Characteristic supports NOTIFY")

                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            }

            else -> {
                return
            }
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, true) == false) {
                Log.d("bluetoothScan", "Set characteristics notification failed")
                return
            }

            writeDescription(cccdDescriptor, payload)
        }
    }

    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray) {
        descriptor.value = payload
        val result = gatt?.writeDescriptor(descriptor)
        Log.d("bluetoothScan", "Writing CCCD: $result")
    }

    private fun findCharacteristic(
        serviceUUID: UUID,
        characteristicsUUID: UUID
    ): BluetoothGattCharacteristic? {
        return gatt?.getService(serviceUUID)?.getCharacteristic(characteristicsUUID)

    }

    override fun reconnect() {
        if (selectedDevice.value != null) {
            selectedDevice.value?.connectGatt(
                context,
                false,
                gattCallback,
                BluetoothDevice.TRANSPORT_LE
            )
        } else {
            startReceiving()
        }

    }

    override fun disconnect() {
        Log.d("bluetoothScan", "Disconnecting")
        gatt?.disconnect()
    }

    override fun startReceiving() {
        Log.d("bluetoothScan", "Scanning")
        if (bluetoothAdapter.isEnabled) {
            coroutineScope.launch {
                data.emit(Resource.Loading(message = "Scanning Ble devices"))
                isScanning = true
                Log.d("bluetoothScan", "Bluetooth Scanner: ${bleScanner}")
                Log.d("bluetoothScan", "Bluetooth Scanner enabled: ${bluetoothAdapter.isEnabled}")

                bleScanner?.startScan(filters, scanSettings, scanCallback)
                delay(4000)
                bleScanner?.stopScan(scanCallback)
                isScanning = false
                Log.d("bluetoothScan", "Scan stopped after delay")

            }
        }
    }

    override fun closeConnection() {
        bleScanner?.stopScan(scanCallback)
        val characteristic =
            findCharacteristic(HEART_RATE_SERVICE_UUID, HEART_RATE_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            disconnectCharacteristic(characteristic)
        }
        gatt?.close()
    }

    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic) {
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid).let { cccdDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, false) == false) {
                Log.d("bluetoothScan", "Set characteristic notification failed")
                return
            }

            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }
}