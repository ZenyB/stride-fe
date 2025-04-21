package com.trio.stride.data.ble

data class HeartRateResult(
    val heartRate: Int,
    val connectionState: ConnectionState
)