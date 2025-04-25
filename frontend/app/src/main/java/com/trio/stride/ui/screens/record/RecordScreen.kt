package com.trio.stride.ui.screens.record

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.locationcomponent.location
import com.trio.stride.R
import com.trio.stride.ui.components.CustomCenterTopAppBar
import com.trio.stride.ui.components.button.FocusUserLocationButton
import com.trio.stride.ui.components.record.RecordValueBlock
import com.trio.stride.ui.components.record.RecordValueBlockType
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatSpeed
import com.trio.stride.ui.utils.formatTimeByMillis
import com.trio.stride.ui.utils.map.RequestLocationPermission
import com.trio.stride.ui.utils.map.checkLocationOn
import com.trio.stride.ui.utils.map.focusToUser
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var permissionRequestCount by remember {
        mutableStateOf(1)
    }
    var showMap by remember {
        mutableStateOf(false)
    }
    var showRequestPermissionButton by remember {
        mutableStateOf(false)
    }
    val distance by viewModel.distance.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle()
    val avgSpeed by viewModel.avgSpeed.collectAsStateWithLifecycle()
    val activityType by viewModel.activityType.collectAsStateWithLifecycle()
    val screenStatus by viewModel.screenStatus.collectAsStateWithLifecycle()
    val recordStatus by viewModel.recordStatus.collectAsStateWithLifecycle()
    val startPoint by viewModel.startPoint.collectAsStateWithLifecycle()
    val mapView by viewModel.mapView.collectAsStateWithLifecycle()
    val locationPoints by viewModel.locationPoints.collectAsStateWithLifecycle()
    val coordinates by viewModel.coordinates.collectAsStateWithLifecycle()
    val mapViewportState = viewModel.mapViewportState

    val startButtonGPSLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "GPS on!", Toast.LENGTH_SHORT).show()
            focusToUser(mapView)
        } else {
            Toast.makeText(context, "Can't access current location", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        focusToUser(mapView)
    }

    Scaffold(
        topBar = {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StrideTheme.colorScheme.background), Alignment.Center
            ) {
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
                                    .size(85.dp),
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
                                                startButtonGPSLauncher
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
                                            .clip(CircleShape)
                                            .background(
                                                StrideTheme.colorScheme.background,
                                                CircleShape
                                            )
                                            .size(85.dp),
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
                                            .size(85.dp),
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
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(vertical = 8.dp)
                                        .background(
                                            StrideTheme.colorScheme.surfaceContainerLowest,
                                            CircleShape
                                        )
                                        .clip(CircleShape)
                                        .size(85.dp),
                                    onClick = { viewModel.stop(context) }) {
                                    Icon(
                                        modifier = Modifier.size(33.dp),
                                        painter = painterResource(R.drawable.filled_round_square_icon),
                                        contentDescription = "Stop record",
                                        tint = StrideTheme.colorScheme.onBackground
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
            when (screenStatus) {
                RecordViewModel.ScreenStatus.DEFAULT -> {

                }

                RecordViewModel.ScreenStatus.DETAIL -> {
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

                RecordViewModel.ScreenStatus.SAVING -> {}

                RecordViewModel.ScreenStatus.SENSOR -> {}
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
