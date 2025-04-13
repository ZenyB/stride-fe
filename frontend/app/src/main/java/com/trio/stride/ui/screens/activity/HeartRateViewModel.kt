package com.trio.stride.ui.screens.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.data.ble.HeartRateReceiveManager
import com.trio.stride.ui.utils.ble.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeartRateViewModel @Inject constructor(
    private val heartRateReceiveManager: HeartRateReceiveManager
) : ViewModel() {
    var initializingMessage by mutableStateOf<String?>(null)
        private set

    var errorMessge by mutableStateOf<String?>(null)
        private set


    var heartRate by mutableIntStateOf(0)
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    private fun subscribeToChanges() {
        viewModelScope.launch {
            heartRateReceiveManager.data.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        heartRate = result.data.heartRate
                    }

                    is Resource.Loading -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessge = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }
                }
            }
        }
    }

    fun reconnect() {
        heartRateReceiveManager.reconnect()
    }

    fun disconnect() {
        heartRateReceiveManager.disconnect()
    }

    fun initializeConnection() {
        errorMessge = null
        subscribeToChanges()
        heartRateReceiveManager.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        heartRateReceiveManager.closeConnection()
    }

}