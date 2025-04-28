package com.trio.stride.ui.screens.record

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.locationcomponent.location
import com.trio.stride.R
import com.trio.stride.ui.components.CustomCenterTopAppBar
import com.trio.stride.ui.components.StatusMessage
import com.trio.stride.ui.components.StatusMessageType
import com.trio.stride.ui.components.button.userlocation.FocusUserLocationButton
import com.trio.stride.ui.components.record.RecordValueBlock
import com.trio.stride.ui.components.record.RecordValueBlockType
import com.trio.stride.ui.screens.record.heartrate.HeartRateView
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.ble.PermissionUtils
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatSpeed
import com.trio.stride.ui.utils.formatTimeByMillis
import com.trio.stride.ui.utils.map.GpsUtils
import com.trio.stride.ui.utils.map.RequestLocationPermission
import com.trio.stride.ui.utils.map.checkLocationOn
import com.trio.stride.ui.utils.map.focusToUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scope = rememberCoroutineScope()
    val permissionState =
        rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)

    val snackbarHostState = remember { SnackbarHostState() }
    var permissionRequestCount by remember {
        mutableStateOf(1)
    }
    var showMap by remember {
        mutableStateOf(false)
    }
    var showRequestPermissionButton by remember {
        mutableStateOf(false)
    }
    var menuExpanded by remember { mutableStateOf(false) }


    val distance by viewModel.distance.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle()
    val avgSpeed by viewModel.avgSpeed.collectAsStateWithLifecycle()
    val activityType by viewModel.activityType.collectAsStateWithLifecycle()
    val screenStatus by viewModel.screenStatus.collectAsStateWithLifecycle()
    val recordStatus by viewModel.recordStatus.collectAsStateWithLifecycle()
    val gpsStatus by viewModel.gpsStatus.collectAsStateWithLifecycle()
    val startPoint by viewModel.startPoint.collectAsStateWithLifecycle()
    val mapView by viewModel.mapView.collectAsStateWithLifecycle()
    val locationPoints by viewModel.locationPoints.collectAsStateWithLifecycle()
    val coordinates by viewModel.coordinates.collectAsStateWithLifecycle()
    val mapViewportState = viewModel.mapViewportState
    val bleConnectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val devices by viewModel.scannedDevices.collectAsStateWithLifecycle()
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsStateWithLifecycle()
    val selectedDevice by viewModel.selectedDevice.collectAsStateWithLifecycle()
    val heartRate by viewModel.heartRate.collectAsState()

    val launcher = GpsUtils.createGpsLauncher(context, mapView, updateGpsStatus = { status ->
        viewModel.updateGpsStatus(status)
    })

//    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
//        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
//        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
//            val state = bluetoothState.getIntExtra(EXTRA_STATE, ERROR)
//            when (state) {
//                STATE_ON -> viewModel.setBluetoothState(true)
//                STATE_OFF -> viewModel.setBluetoothState(false)
//            }
//        }
//    }

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    BackHandler {
        if (screenStatus != RecordViewModel.ScreenStatus.DEFAULT) {
            viewModel.handleBackToDefault()
        } else {
            backDispatcher?.onBackPressed()
        }
    }

    LaunchedEffect(Unit) {
        focusToUser(mapView)
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                screenStatus == RecordViewModel.ScreenStatus.DEFAULT,
                enter = slideInVertically(
                    initialOffsetY = { -it }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it }
                )
            ) {
                CustomCenterTopAppBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                    title = "Run",
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.park_down_icon),
                                contentDescription = "Close record screen",
                                tint = StrideTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.setting_icon),
                                contentDescription = "Setting",
                                tint = StrideTheme.colorScheme.onBackground
                            )
                        }
                    })
            }
        },
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .padding(bottom = 52.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .size(44.dp)
                        .background(StrideTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape),
                    onClick = {}
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(R.drawable.layers_icon),
                        contentDescription = "Select map type",
                        tint = StrideTheme.colorScheme.onBackground
                    )
                }
                mapView?.let { mv ->
                    FocusUserLocationButton(mapView = mv)
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StrideTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (recordStatus == RecordViewModel.RecordStatus.NONE || recordStatus == RecordViewModel.RecordStatus.STOP) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .size(40.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(),
                                ) {

                                },
                            painter = painterResource(R.drawable.run_icon),
                            contentDescription = "Choose activity type"
                        )
                        Icon(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .size(40.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(),
                                ) {
                                    viewModel.handleShowSensorView()
                                },
                            painter = painterResource(R.drawable.heart_pulse),
                            contentDescription = "Show sensor"
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        ),
                    Alignment.Center
                ) {
                    when (recordStatus) {
                        RecordViewModel.RecordStatus.NONE ->
                            TextButton(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clip(CircleShape)
                                    .background(StrideTheme.colorScheme.secondary, CircleShape)
                                    .size(95.dp),
                                colors = ButtonDefaults.textButtonColors().copy(
                                    containerColor = StrideTheme.colorScheme.secondary,
                                    contentColor = StrideTheme.colorScheme.onSecondary
                                ),
                                onClick = {
                                    if (mapView != null) {
                                        var userLocation: Point? = null
                                        mapView?.location?.addOnIndicatorPositionChangedListener { point ->
                                            userLocation = point
                                        }
                                        if (userLocation != null)
                                            viewModel.startRecord(userLocation, context)
                                        else
                                            checkLocationOn(
                                                context,
                                                mapView,
                                                launcher
                                            )
                                    }
                                }) {
                                Text(
                                    "START",
                                    color = StrideTheme.colorScheme.onSecondary,
                                    style = StrideTheme.typography.titleMedium
                                )
                            }

                        RecordViewModel.RecordStatus.STOP -> {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 4.dp),
                                    Alignment.CenterEnd
                                ) {
                                    TextButton(
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                StrideTheme.colorScheme.surfaceContainerLowest,
                                                CircleShape
                                            )
                                            .size(95.dp),
                                        onClick = { viewModel.resume(context) }) {
                                        Text(
                                            "RESUME",
                                            color = StrideTheme.colorScheme.onBackground,
                                            style = StrideTheme.typography.titleMedium
                                        )
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Row(
                                    Modifier
                                        .weight(1f)
                                        .padding(start = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                StrideTheme.colorScheme.secondary,
                                                CircleShape
                                            )
                                            .size(95.dp),
                                        onClick = { viewModel.finish(context) }) {
                                        Text(
                                            "FINISH",
                                            color = StrideTheme.colorScheme.onSecondary,
                                            style = StrideTheme.typography.titleMedium
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    val isVisibleMetric =
                                        screenStatus == RecordViewModel.ScreenStatus.DETAIL
                                    val showMetricButtonContainerColor =
                                        if (isVisibleMetric)
                                            StrideTheme.colorScheme.surfaceContainerLowest
                                        else
                                            StrideTheme.colorScheme.secondary
                                    val showMetricButtonContentColor =
                                        if (isVisibleMetric)
                                            StrideTheme.colorScheme.secondary
                                        else
                                            StrideTheme.colorScheme.onSecondary

                                    IconButton(modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            showMetricButtonContainerColor,
                                            CircleShape
                                        )
                                        .clip(CircleShape),
                                        colors = IconButtonDefaults.iconButtonColors().copy(
                                            containerColor = showMetricButtonContainerColor,
                                            contentColor = showMetricButtonContentColor
                                        ),
                                        onClick = { viewModel.handleVisibleMetric() }) {
                                        Icon(
                                            modifier = Modifier.size(28.dp),
                                            painter = painterResource(R.drawable.location_outline_icon),
                                            contentDescription = "Handle visible metric",
                                            tint = showMetricButtonContentColor
                                        )
                                    }
                                }
                            }
                        }

                        RecordViewModel.RecordStatus.RECORDING -> {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(vertical = 8.dp)
                                        .background(
                                            StrideTheme.colorScheme.secondary,
                                            CircleShape
                                        )
                                        .clip(CircleShape)
                                        .size(95.dp),
                                    colors = ButtonDefaults.buttonColors().copy(
                                        containerColor = StrideTheme.colorScheme.secondary
                                    ),
                                    onClick = { viewModel.stop(context) }) {
                                    Icon(
                                        modifier = Modifier.size(33.dp),
                                        painter = painterResource(R.drawable.filled_round_square_icon),
                                        contentDescription = "Stop record",
                                        tint = StrideTheme.colorScheme.surfaceContainerLowest
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                val isVisibleMetric =
                                    screenStatus == RecordViewModel.ScreenStatus.DETAIL
                                val showMetricButtonContainerColor =
                                    if (isVisibleMetric)
                                        StrideTheme.colorScheme.surfaceContainerLowest
                                    else
                                        StrideTheme.colorScheme.secondary
                                val showMetricButtonContentColor =
                                    if (isVisibleMetric)
                                        StrideTheme.colorScheme.secondary
                                    else
                                        StrideTheme.colorScheme.onSecondary

                                IconButton(modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                                    .size(44.dp)
                                    .background(
                                        showMetricButtonContainerColor,
                                        CircleShape
                                    )
                                    .clip(CircleShape),
                                    colors = IconButtonDefaults.iconButtonColors().copy(
                                        containerColor = showMetricButtonContainerColor,
                                        contentColor = showMetricButtonContentColor
                                    ),
                                    onClick = { viewModel.handleVisibleMetric() }) {
                                    Icon(
                                        modifier = Modifier.size(28.dp),
                                        painter = painterResource(R.drawable.location_outline_icon),
                                        contentDescription = "Handle visible metric",
                                        tint = showMetricButtonContentColor
                                    )
                                }
                            }
                        }

                        RecordViewModel.RecordStatus.FINISH -> {}
                    }
                }
            }
        }
    ) { padding ->
        RequestLocationPermission(
            requestCount = permissionRequestCount,
            onPermissionDenied = {
                scope.launch {
                    snackbarHostState.showSnackbar("You need to accept location permissions.")
                }
                showRequestPermissionButton = true
            },
            onPermissionReady = {
                showRequestPermissionButton = false
                showMap = true
            }
        )
        Box() {
            MapboxMap(
                Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                mapViewportState = mapViewportState,
                style = { MapStyle(style = Style.MAPBOX_STREETS) }
            ) {
                MapEffect(Unit) { mv ->
                    viewModel.setMapView(mv)
                    viewModel.drawRoute(mv, emptyList())
                    viewModel.reloadMapStyle()
                    viewModel.enableUserLocation()
                }
                if (startPoint != null) {
                    CircleAnnotation(point = startPoint!!) {
                        circleRadius = 5.0
                        circleColor = StrideColor.green600
                        circleStrokeWidth = 1.5
                        circleStrokeColor = Color(0xffffffff)
                    }
                }
            }

            if (screenStatus == RecordViewModel.ScreenStatus.DEFAULT) {
                if (recordStatus == RecordViewModel.RecordStatus.NONE)
                    GPSStatusMessage(
                        Modifier.padding(top = padding.calculateTopPadding()),
                        gpsStatus
                    )
                if (recordStatus == RecordViewModel.RecordStatus.STOP)
                    StatusMessage(
                        text = "STOP",
                        type = StatusMessageType.ERROR,
                        Modifier.padding(top = padding.calculateTopPadding())
                    )
            }


            AnimatedVisibility(
                screenStatus == RecordViewModel.ScreenStatus.DETAIL,
                enter = slideInHorizontally(
                    initialOffsetX = { -it }
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(StrideTheme.colorScheme.background)
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    RecordValueBlock(
                        title = "Time",
                        value = formatTimeByMillis(time)
                    )
                    RecordValueBlock(
                        type = RecordValueBlockType.Large,
                        title = "Avg Speed",
                        value = formatSpeed(avgSpeed),
                        unit = "km/h"
                    )
                    RecordValueBlock(
                        title = "Distance",
                        value = formatDistance(distance),
                        unit = "km"
                    )
                }
            }

            when (screenStatus) {
                RecordViewModel.ScreenStatus.DEFAULT -> {

                }

                RecordViewModel.ScreenStatus.DETAIL -> {

                }

                RecordViewModel.ScreenStatus.SAVING -> {}

                RecordViewModel.ScreenStatus.SENSOR -> {

                }
            }

            if (showRequestPermissionButton) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                permissionRequestCount += 1
                            }
                        ) {
                            Text("Request permission again ($permissionRequestCount)")
                        }
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)

                                    )
                                )
                            }
                        ) {
                            Text("Show App Settings page")
                        }
                    }
                }
            }
        }
    }
    AnimatedVisibility(
        screenStatus == RecordViewModel.ScreenStatus.SENSOR,
        enter = slideInVertically(
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }
        )
    ) {
        HeartRateView(
            bleConnectionState = bleConnectionState,
            devices = devices,
            isBluetoothOn = isBluetoothOn,
            selectedDevice = selectedDevice,
            heartRate = heartRate,
            connectDevice = { device ->
                viewModel.connectToDevice(context, device)
            },
            reconnect = {
                viewModel.reconnect(context)
            },
            disconnect = {
                viewModel.disconnect(context)
            },
            initializeConnection = {
                viewModel.initializeConnection(context)
            }
        )
    }
}

@Composable
private fun GPSStatusMessage(modifier: Modifier = Modifier, gpsStatus: RecordViewModel.GPSStatus) {
    val showMessage = remember { mutableStateOf(true) }

    LaunchedEffect(gpsStatus) {
        if (gpsStatus == RecordViewModel.GPSStatus.GPS_READY) {
            delay(5000)
            showMessage.value = false
        } else {
            showMessage.value = true
        }
    }
    when (gpsStatus) {
        RecordViewModel.GPSStatus.NO_GPS -> {
            StatusMessage("NO GPS SIGNAL", StatusMessageType.ERROR, modifier)
        }

        RecordViewModel.GPSStatus.ACQUIRING_GPS -> {
            StatusMessage("ACQUIRING GPS...", StatusMessageType.PROCESSING, modifier)
        }

        RecordViewModel.GPSStatus.GPS_READY -> {
            if (showMessage.value)
                StatusMessage("GPS SIGNAL ACQUIRED", StatusMessageType.SUCCESS, modifier)
        }
    }
}

