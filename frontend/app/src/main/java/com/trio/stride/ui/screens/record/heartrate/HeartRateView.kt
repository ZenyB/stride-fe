package com.trio.stride.ui.screens.record.heartrate

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.trio.stride.R
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.ui.components.StatusMessage
import com.trio.stride.ui.components.StatusMessageType
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.RequestNotificationPermission
import com.trio.stride.ui.utils.ble.PermissionUtils

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeartRateView(
    bleConnectionState: ConnectionState,
    devices: List<BluetoothDevice>,
    isBluetoothOn: Boolean,
    selectedDevice: BluetoothDevice?,
    heartRate: Int,
    connectDevice: (BluetoothDevice) -> Unit,
    reconnect: () -> Unit,
    disconnect: () -> Unit,
    initializeConnection: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionState =
        rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)


    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionState.launchMultiplePermissionRequest()
                if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                    reconnect()
                }
            }

            if (event == Lifecycle.Event.ON_STOP) {
                if (bleConnectionState == ConnectionState.Connected) {
                    //TODO
//                    viewModel.disconnect()
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
            Log.d("bluetoothScan", "in launch effect $bleConnectionState")
            if (bleConnectionState == ConnectionState.Uninitialized) {
                initializeConnection()
            }
        }
    }

    Scaffold(modifier = Modifier.background(StrideTheme.colors.white)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                "Sensors",
                style = StrideTheme.typography.headlineSmall,
            )

            Spacer(Modifier.height(16.dp))
            if (isBluetoothOn) {
                Text(
                    "One heart rate sensor can be connected at a time",
                    style = StrideTheme.typography.labelLarge,
                )
            }

            if (!isBluetoothOn) {
                StatusMessage(text = "bluetooth off", type = StatusMessageType.ERROR)
            }
            Spacer(Modifier.height(32.dp))
            Text(
                "Available sensors".uppercase(),
                style = StrideTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                textAlign = TextAlign.Start
            )
            RequestNotificationPermission(
                onPermissionGranted = {
                    Log.d("bluetoothScan", "notification permission granted")
                },
                onPermissionDenied = {
                    Log.d("bluetoothScan", "notification permission denied")
                }
            )

            Spacer(Modifier.height(16.dp))
            LazyColumn {
                itemsIndexed(devices) { index, device ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                connectDevice(device)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.heart_pulse),
                                contentDescription = "heart pulse",
                                tint = if (bleConnectionState == ConnectionState.Connected)
                                    StrideTheme.colorScheme.primary
                                else
                                    Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = device.name ?: "Unnamed",
                                    style = StrideTheme.typography.bodyLarge,
                                )
                                if (device.address == selectedDevice?.address
                                    && bleConnectionState == ConnectionState.Connected
                                ) {
                                    Text(
                                        text = "Heart rate: $heartRate",
                                        style = StrideTheme.typography.labelMedium,
                                        color = StrideTheme.colors.gray600
                                    )
                                }
                            }
                        }

                        if (device.address == selectedDevice?.address) {
                            if (bleConnectionState == ConnectionState.CurrentlyInitializing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = StrideTheme.colorScheme.primary,
                                    strokeCap = StrokeCap.Round,
                                    strokeWidth = 2.dp
                                )
                            } else if (bleConnectionState == ConnectionState.Connected) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Connected",
                                        style = StrideTheme.typography.labelMedium,
                                        color = StrideTheme.colors.gray600
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    IconButton(onClick = { menuExpanded = true }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ellipsis_more),
                                            contentDescription = "More Options",
                                            tint = StrideTheme.colorScheme.primary
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Disconnect") },
                                            onClick = {
                                                menuExpanded = false
                                                disconnect()
                                            },
                                            contentPadding = PaddingValues(
                                                horizontal = 12.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }
    }
}