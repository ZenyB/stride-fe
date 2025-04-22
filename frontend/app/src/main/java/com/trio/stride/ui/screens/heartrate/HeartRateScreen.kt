package com.trio.stride.ui.screens.heartrate

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ERROR
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.STATE_OFF
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.trio.stride.R
import com.trio.stride.RecordService
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.ui.components.StatusMessage
import com.trio.stride.ui.components.StatusMessageType
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.RequestNotificationPermission
import com.trio.stride.ui.utils.ble.PermissionUtils
import com.trio.stride.ui.utils.ble.SystemBroadcastReceiver

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeartRateScreen(
    viewModel: HeartRateViewModel
) {
    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = bluetoothState.getIntExtra(EXTRA_STATE, ERROR)
            when (state) {
                STATE_ON -> viewModel.setBluetoothState(true)
                STATE_OFF -> viewModel.setBluetoothState(false)
            }
        }
    }
    val context = LocalContext.current

    val permissionState =
        rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)

    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val devices by viewModel.scannedDevices.collectAsStateWithLifecycle()
    var menuExpanded by remember { mutableStateOf(false) }
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsStateWithLifecycle()
    val selectedDeviceAddress by viewModel.selectedDeviceAddress.collectAsStateWithLifecycle()

    val heartRate by viewModel.heartRate.collectAsState()


    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionState.launchMultiplePermissionRequest()
                if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                    viewModel.reconnect(context)
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

    LaunchedEffect(key1 = permissionState.allPermissionsGranted, key2 = isBluetoothOn) {
        if (permissionState.allPermissionsGranted) {
            if (bleConnectionState == ConnectionState.Uninitialized) {
                viewModel.initializeConnection(context)
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

            Button(onClick = {
                viewModel.startWorkout(context)
            }) {
                Text("Start Workout")
            }

            Button(onClick = {
                viewModel.stopWorkout(context)
            }) {
                Text("Stop Workout")
            }

            Spacer(Modifier.height(16.dp))
            LazyColumn {
                itemsIndexed(devices) { index, device ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.connectToDevice(context, device)
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
                                if (device.address == selectedDeviceAddress) {
                                    Text(
                                        text = "Heart rate: ${heartRate}",
                                        style = StrideTheme.typography.labelMedium,
                                        color = StrideTheme.colors.gray600
                                    )
                                }
                            }
                        }

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
                                            viewModel.disconnect(context)
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