package com.trio.stride.data.ble

import com.trio.stride.ui.utils.ble.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface HeartRateReceiveManager {
    val data: MutableSharedFlow<Resource<HeartRateResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()
}