package com.trio.stride.ui.screens.record

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.locationcomponent.location
import com.trio.stride.R
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.ui.components.CustomCenterTopAppBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.StatusMessage
import com.trio.stride.ui.components.StatusMessageType
import com.trio.stride.ui.components.button.userlocation.FocusUserLocationButton
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.components.map.mapstyle.MapStyleBottomSheet
import com.trio.stride.ui.components.map.mapstyle.MapStyleViewModel
import com.trio.stride.ui.components.record.GPSStatusMessage
import com.trio.stride.ui.components.record.RecordButton
import com.trio.stride.ui.components.record.RecordValueBlock
import com.trio.stride.ui.components.record.RecordValueBlockType
import com.trio.stride.ui.components.sport.bottomsheet.SportBottomSheetWithCategory
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportIconButton
import com.trio.stride.ui.screens.activity.detail.ActivityFormMode
import com.trio.stride.ui.screens.activity.detail.ActivityFormView
import com.trio.stride.ui.screens.record.heartrate.HeartRateView
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.PermissionViewModel
import com.trio.stride.ui.utils.RequestNotificationPermission
import com.trio.stride.ui.utils.advancedShadow
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatSpeed
import com.trio.stride.ui.utils.formatTimeByMillis
import com.trio.stride.ui.utils.map.GpsUtils
import com.trio.stride.ui.utils.map.RequestLocationPermission
import com.trio.stride.ui.utils.map.checkLocationOn
import com.trio.stride.ui.utils.map.focusToUser
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordScreen(
    back: () -> Unit,
    navController: NavController,
    mapStyleViewModel: MapStyleViewModel = hiltViewModel(),
    viewModel: RecordViewModel = hiltViewModel(),
    permissionViewModel: PermissionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    var showLocationRequest by remember { mutableStateOf(true) }
    var showNotificationRequest by remember { mutableStateOf(true) }
    var permissionRequestCount by remember {
        mutableStateOf(1)
    }
    var showMap by remember {
        mutableStateOf(false)
    }
    var locationGranted by remember { mutableStateOf(false) }
    var notificationRequested by remember { mutableStateOf(false) }

    var showSheet by remember { mutableStateOf(false) }

    var showSportBottomSheet by remember { mutableStateOf(false) }

    val mapStyle by mapStyleViewModel.mapStyle.collectAsStateWithLifecycle()

    val distance by viewModel.distance.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle()
    val avgSpeed by viewModel.avgSpeed.collectAsStateWithLifecycle()
    val screenStatus by viewModel.screenStatus.collectAsStateWithLifecycle()
    val recordStatus by viewModel.recordStatus.collectAsStateWithLifecycle()
    val gpsStatus by viewModel.gpsStatus.collectAsStateWithLifecycle()
    val startPoint by viewModel.startPoint.collectAsStateWithLifecycle()
    val mapView by viewModel.mapView.collectAsStateWithLifecycle()
    val mapViewportState = viewModel.mapViewportState.collectAsStateWithLifecycle()
    val bleConnectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val devices by viewModel.scannedDevices.collectAsStateWithLifecycle()
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsStateWithLifecycle()
    val selectedDevice by viewModel.selectedDevice.collectAsStateWithLifecycle()
    val heartRate by viewModel.heartRate.collectAsState()
    val currentSport by viewModel.currentSport.collectAsStateWithLifecycle()
    val sportsByCategory by viewModel.sportsByCategory.collectAsState()
    val locationPermissionCount by permissionViewModel.locationCount.collectAsStateWithLifecycle()

    val launcher = GpsUtils.createGpsLauncher(context, mapView, updateGpsStatus = { status ->
        viewModel.updateGpsStatus(status)
    })

    BackHandler {
        if (!state.isLoading) {
            val isNotDefault = screenStatus != RecordViewModel.ScreenStatus.DEFAULT
            val isSaving = screenStatus == RecordViewModel.ScreenStatus.SAVING
            if (isNotDefault) {
                if (isSaving)
                    viewModel.handleDismissSaveActivity(context)
                else
                    viewModel.handleBackToDefault()
            } else {
                back()
            }
        } else {

        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                lifecycleOwner.lifecycleScope.launch {
                    val mapInstance = snapshotFlow { mapView }
                        .filterNotNull()
                        .first()
                    focusToUser(mapInstance)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    if (state.isLoading) {
        Loading()
    }

    StrideDialog(
        visible = state.isSavingError,
        title = "Save activity error",
        description = "There are some error, try again later.",
        dismiss = { viewModel.resetSaveActivityError() },
        dismissText = "Cancel",
        doneText = "Try Again",
        done = { viewModel.saveAgain(context) },
    )

    StrideDialog(
        visible = state.isNotEnoughDataToSave,
        title = "Not moving yet?",
        description = "Stride need a longer activity to upload. Please continue or discard this activity.",
        dismiss = { viewModel.setIsNotEnoughDataToSave(false) },
        destructiveText = "Discard",
        neutralText = "Resume",
        neutral = {
            viewModel.resume(context)
        },
        destructive = {
            viewModel.discard(context)
            back()
        },
    )

    Scaffold(
        containerColor = StrideTheme.colors.transparent,
        topBar = {
            AnimatedVisibility(
                screenStatus == RecordViewModel.ScreenStatus.DEFAULT
                        || (screenStatus == RecordViewModel.ScreenStatus.DETAIL
                        && recordStatus == RecordViewModel.RecordStatus.STOP),
                enter = slideInVertically(
                    initialOffsetY = { -it }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it }
                )
            ) {
                CustomCenterTopAppBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                    title = currentSport?.name ?: "",
                    navigationIcon = {
                        IconButton(onClick = { back() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.park_down_icon),
                                contentDescription = "Close record screen",
                                tint = StrideTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.handleShowSensorView() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.heart_pulse),
                                contentDescription = "Show sensor",
                                tint = StrideTheme.colorScheme.onBackground
                            )
                        }
                    })
            }
        },
        floatingActionButton = {
            if (locationGranted && currentSport?.sportMapType != SportMapType.NO_MAP && screenStatus == RecordViewModel.ScreenStatus.DEFAULT) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 52.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { showSheet = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = StrideTheme.colors.white
                        ),
                        modifier = Modifier
                            .size(44.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.layers_icon),
                            contentDescription = "Map option",
                            tint = Color.Black
                        )
                    }
                    mapView?.let { mv ->
                        FocusUserLocationButton(mapView = mv)
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        StrideTheme.colorScheme.surface,
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .animateContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (screenStatus != RecordViewModel.ScreenStatus.SAVING && screenStatus != RecordViewModel.ScreenStatus.SENSOR) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .padding(
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding()
                            ),
                        Alignment.Center
                    ) {
                        when (recordStatus) {
                            RecordViewModel.RecordStatus.NONE -> {
                                Box(Modifier.fillMaxWidth(), Alignment.CenterStart) {
                                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                                        RecordButton(
                                            onClick = {
                                                if (mapView != null) {
                                                    var userLocation: Point? = null
                                                    mapView?.location?.addOnIndicatorPositionChangedListener { point ->
                                                        userLocation = point
                                                    }
                                                    if (userLocation != null) {
                                                        focusToUser(mapView)
                                                        viewModel.startRecord(
                                                            context,
                                                            userLocation!!
                                                        )
                                                    } else
                                                        checkLocationOn(
                                                            context,
                                                            mapView,
                                                            launcher
                                                        )
                                                }

                                                if (currentSport?.sportMapType == SportMapType.NO_MAP) {
                                                    viewModel.startRecord(context)
                                                }
                                            }) {
                                            Text(
                                                "START",
                                                style = StrideTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                    if (currentSport != null) {
                                        Box(
                                            Modifier
                                                .padding(start = 48.dp)
                                                .align(Alignment.CenterStart)
                                        ) {
                                            ChooseSportIconButton(
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        StrideTheme.colors.transparent,
                                                        CircleShape
                                                    ),
                                                iconModifier = Modifier
                                                    .size(28.dp)
                                                    .zIndex(100f),
                                                iconImage = currentSport!!.image,
                                                onClick = { showSportBottomSheet = true }
                                            )
                                        }
                                    }
                                }
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
                                        RecordButton(
                                            isPrimary = false,
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
                                        RecordButton(
                                            onClick = { viewModel.finish(context) }) {
                                            Text(
                                                "FINISH",
                                                color = StrideTheme.colorScheme.onSecondary,
                                                style = StrideTheme.typography.titleMedium
                                            )
                                        }
                                        if (currentSport?.sportMapType != SportMapType.NO_MAP) {
                                            Spacer(Modifier.width(8.dp))
                                            val isVisibleMetric =
                                                screenStatus == RecordViewModel.ScreenStatus.DETAIL
                                            val showMetricButtonContainerColor =
                                                if (isVisibleMetric)
                                                    StrideTheme.colorScheme.surface
                                                else
                                                    StrideTheme.colorScheme.secondary
                                            val showMetricButtonContentColor =
                                                if (isVisibleMetric)
                                                    StrideTheme.colorScheme.secondary
                                                else
                                                    StrideTheme.colorScheme.onSecondary

                                            IconButton(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .advancedShadow(
                                                        cornersRadius = 1000.dp
                                                    )
                                                    .background(
                                                        showMetricButtonContainerColor,
                                                        CircleShape
                                                    )
                                                    .clip(CircleShape),
                                                colors = IconButtonDefaults.iconButtonColors()
                                                    .copy(
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
                            }

                            RecordViewModel.RecordStatus.RECORDING -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RecordButton(
                                        modifier = Modifier.align(Alignment.Center),
                                        isPrimary = true,
                                        onClick = { viewModel.stop(context) }) {
                                        Icon(
                                            modifier = Modifier.size(33.dp),
                                            painter = painterResource(R.drawable.filled_round_square_icon),
                                            contentDescription = "Stop record",
                                        )
                                    }
                                    if (currentSport?.sportMapType != SportMapType.NO_MAP) {
                                        Spacer(Modifier.width(8.dp))
                                        val isVisibleMetric =
                                            screenStatus == RecordViewModel.ScreenStatus.DETAIL
                                        val showMetricButtonContainerColor =
                                            if (isVisibleMetric)
                                                StrideTheme.colorScheme.surface
                                            else
                                                StrideTheme.colorScheme.secondary
                                        val showMetricButtonContentColor =
                                            if (isVisibleMetric)
                                                StrideTheme.colorScheme.secondary
                                            else
                                                StrideTheme.colorScheme.onSecondary

                                        IconButton(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 16.dp)
                                                .size(44.dp)
                                                .advancedShadow(
                                                    cornersRadius = 1000.dp
                                                )
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

                            RecordViewModel.RecordStatus.FINISH -> {}
                        }
                    }
                }
            }
        }
    ) { padding ->
        val bottomPadding =
            if (padding.calculateBottomPadding() - 24.dp > 0.dp) padding.calculateBottomPadding() - 24.dp else 0.dp
        Box(modifier = Modifier.fillMaxSize()) {
            if (currentSport?.sportMapType != SportMapType.NO_MAP) {
                RequestLocationPermission(
                    showRequest = showLocationRequest,
                    onPermissionDenied = {
                        scope.launch {
                            snackbarHostState.showSnackbar("You need to accept location permissions.")
                        }
                        locationGranted = false
                        showLocationRequest = false
                    },
                    onPermissionReady = {
                        locationGranted = true
                    }
                )
            }
            if (!notificationRequested) {
                RequestNotificationPermission(
                    showRequest = showNotificationRequest,
                    onPermissionGranted = {
                        Log.d("bluetoothScan", "notification permission granted")
                        scope.launch {
                            snackbarHostState.showSnackbar("You need to accept notification permissions to tracking your activity on status bar.")
                        }
                        notificationRequested = true
                    },
                    onPermissionDenied = {
                        Log.d("bluetoothScan", "notification permission denied")
                        notificationRequested = true
                        showNotificationRequest = false
                    }
                )
            }

            if (!locationGranted && currentSport?.sportMapType != SportMapType.NO_MAP && recordStatus == RecordViewModel.RecordStatus.NONE) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text(
                            "Please turn on location permission to continue with this feature.",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 32.dp),
                            textAlign = TextAlign.Center,
                            style = StrideTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                showLocationRequest = true
                            }
                        ) {
                            Text(
                                "Try again",
                                style = StrideTheme.typography.titleMedium
                            )
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
                            Text(
                                "Go to App Settings page",
                                style = StrideTheme.typography.titleMedium
                            )
                        }
                    }
                }
            } else {
                Box {
                    if (currentSport?.sportMapType != SportMapType.NO_MAP) {
                        MapboxMap(
                            Modifier
                                .fillMaxSize()
                                .padding(top = padding.calculateTopPadding())
                                .padding(bottom = bottomPadding),
                            mapViewportState = mapViewportState.value,
                            style = { MapStyle(style = mapStyle) },
                        ) {
                            if (startPoint != null) {
                                CircleAnnotation(point = startPoint!!) {
                                    circleRadius = 5.0
                                    circleColor = StrideColor.green600
                                    circleStrokeWidth = 1.5
                                    circleStrokeColor = Color(0xffffffff)
                                }
                            }

                            MapEffect(Unit) { mv ->
                                viewModel.setMapView(mv)
                                viewModel.reloadMapStyle()
                                viewModel.enableUserLocation()
                            }

                            MapEffect(mapStyle) {
                                viewModel.reloadMapStyle()
                            }
                        }
                    }

                    if ((screenStatus == RecordViewModel.ScreenStatus.DEFAULT
                                || (screenStatus == RecordViewModel.ScreenStatus.DETAIL
                                && recordStatus == RecordViewModel.RecordStatus.STOP))
                        && currentSport?.sportMapType != SportMapType.NO_MAP
                    ) {
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
                }

                AnimatedVisibility(
                    visible = (screenStatus == RecordViewModel.ScreenStatus.DETAIL
                            && recordStatus == RecordViewModel.RecordStatus.RECORDING)
                            || currentSport?.sportMapType == SportMapType.NO_MAP,
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
                            .background(StrideTheme.colorScheme.surface)
                            .padding(bottom = padding.calculateBottomPadding() + 16.dp)
                            .windowInsetsPadding(WindowInsets.statusBars),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        if (currentSport?.sportMapType != SportMapType.NO_MAP) {
                            RecordValueBlock(
                                title = "Time",
                                value = if (time == 0L) "--" else formatTimeByMillis(time)
                            )
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = StrideTheme.colors.grayBorder
                            )
                            RecordValueBlock(
                                title = "Avg Speed",
                                value = if (avgSpeed == 0.0) "--" else formatSpeed(avgSpeed),
                                unit = "km/h",
                                type = RecordValueBlockType.Large
                            )
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = StrideTheme.colors.grayBorder
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                RecordValueBlock(
                                    modifier = Modifier.weight(1f),
                                    title = "Distance",
                                    value = if (distance < 0.1) "--" else formatDistance(
                                        distance
                                    ),
                                    unit = "km"
                                )
                                VerticalDivider(
                                    thickness = 1.dp,
                                    color = StrideTheme.colors.grayBorder
                                )
                                RecordValueBlock(
                                    modifier = Modifier.weight(1f),
                                    title = "Heart Rate",
                                    value = if (heartRate == 0) "--" else heartRate.toString(),
                                    unit = "BPM"
                                )
                            }
                        } else {
                            RecordValueBlock(
                                modifier = Modifier.padding(top = padding.calculateTopPadding()),
                                type = RecordValueBlockType.Large,
                                title = "Time",
                                value = if (time == 0L) "--" else formatTimeByMillis(time),
                            )
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = StrideTheme.colors.grayBorder
                            )
                            RecordValueBlock(
                                type = RecordValueBlockType.Large,
                                title = "Heart Rate",
                                value = if (heartRate == 0) "--" else heartRate.toString(),
                                unit = "BPM"
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = bottomPadding),
                    visible = (screenStatus == RecordViewModel.ScreenStatus.DETAIL
                            && recordStatus == RecordViewModel.RecordStatus.STOP
                            && currentSport?.sportMapType != SportMapType.NO_MAP),
                    enter = slideInHorizontally(
                        initialOffsetX = { -it }
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it }
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StrideTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RecordValueBlock(
                                    modifier = Modifier.weight(1f),
                                    title = "Time",
                                    value = if (time == 0L) "--" else formatTimeByMillis(time),
                                    type = RecordValueBlockType.OnMapSmall
                                )
                                RecordValueBlock(
                                    modifier = Modifier.weight(1f),
                                    title = "Heart Rate",
                                    unit = "BPM",
                                    value = if (heartRate == 0) "--" else heartRate.toString(),
                                    type = RecordValueBlockType.OnMapSmall
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RecordValueBlock(
                                    modifier = Modifier.weight(1f),
                                    title = "Avg speed",
                                    unit = "km/h",
                                    value = if (avgSpeed == 0.0) "--" else formatSpeed(avgSpeed),
                                    type = RecordValueBlockType.OnMapSmall
                                )
                                RecordValueBlock(
                                    modifier = Modifier.weight(1f),
                                    title = "Distance",
                                    unit = "km",
                                    value = if (distance < 0.1) "--" else formatDistance(
                                        distance
                                    ),
                                    type = RecordValueBlockType.OnMapSmall
                                )
                            }
                            Spacer(Modifier.height(24.dp))
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

        AnimatedVisibility(
            screenStatus == RecordViewModel.ScreenStatus.SAVING,
            enter = slideInVertically(
                initialOffsetY = { it }
            ),
            exit = slideOutVertically(
                targetOffsetY = { it }
            )
        ) {
            ActivityFormView(
                "Save",
                "SAVE",
                mode = ActivityFormMode.Create(
                    sportFromRecord = currentSport,
                    onCreate = { dto, sport ->
                        viewModel.saveActivity(dto, sport, context, {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "refresh",
                                true
                            )
                            back()
                        })
                    },
                    onDiscard = {
                        viewModel.discard(context)
                        back()
                    }
                ),
                dismissAction = { viewModel.handleDismissSaveActivity(context) },
                isSaving = state.isLoading,
            )
        }

        if (showSheet) {
            MapStyleBottomSheet(
                mapStyle = mapStyle,
                onMapStyleSelected = { mapStyleViewModel.selectStyle(it) },
                onDismiss = { showSheet = false }
            )
        }

        currentSport?.let {
            SportBottomSheetWithCategory(
                sportsByCategory = sportsByCategory,
                selectedSport = it,
                onItemClick = { sport ->
                    viewModel.updateCurrentSport(sport)
                    showSportBottomSheet = false
                },
                dismissAction = { showSportBottomSheet = false },
                visible = showSportBottomSheet
            )
        }
    }
}

