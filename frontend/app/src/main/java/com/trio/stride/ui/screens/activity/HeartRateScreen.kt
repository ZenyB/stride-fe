package com.trio.stride.ui.screens.activity

import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.ui.utils.ble.PermissionUtils
import com.trio.stride.ui.utils.ble.SystemBroadcastReceiver

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeartRateScreen(
    onBluetoothStateChanged: () -> Unit,
    viewModel: HeartRateViewModel = hiltViewModel()
) {
    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            onBluetoothStateChanged()
        }
    }
    val permissionState =
        rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)

    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionState.launchMultiplePermissionRequest()
                if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                    viewModel.reconnect()
                }
            }

            if (event == Lifecycle.Event.ON_STOP) {
                if (bleConnectionState == ConnectionState.Connected) {
                    viewModel.disconnect()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    LaunchedEffect(key1 = permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            if (bleConnectionState == ConnectionState.Uninitialized) {
                viewModel.initializeConnection()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (bleConnectionState == ConnectionState.CurrentlyInitializing) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                    if (viewModel.initializingMessage != null) {
                        Text(text = viewModel.initializingMessage!!)
                    }
                }
            } else if (!permissionState.allPermissionsGranted) {
                Text(text = "Go to the app setting and allow missing permissions")
            } else if (viewModel.errorMessge != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = viewModel.errorMessge!!)
                }

                Button(onClick = {
                    if (permissionState.allPermissionsGranted) {
                        viewModel.initializeConnection()
                    }
                }) {
                    Text(text = "Try again")
                }
            } else if (bleConnectionState==ConnectionState.Connected){
                Column (modifier = Modifier.fillMaxSize()){
                    Text(text = "Heart rate: ${viewModel.heartRate}")
                }
            }
        }
    }
}