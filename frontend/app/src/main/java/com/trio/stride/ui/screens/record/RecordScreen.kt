package com.trio.stride.ui.screens.record

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
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
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.trio.stride.R
import com.trio.stride.ui.components.CustomCenterTopAppBar
import com.trio.stride.ui.components.record.RecordValueBlock
import com.trio.stride.ui.components.record.RecordValueBlockType
import com.trio.stride.ui.screens.maps.view.ZOOM
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatTimeByMillis
import com.trio.stride.ui.utils.map.RequestLocationPermission
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(106.80259579, 10.87007182))
            zoom(ZOOM)
            pitch(0.0)
        }
    }
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
                CurrentLocationButton(
                    mapViewportState = mapViewportState,
                    modifier = Modifier
                        .size(44.dp)
                        .background(StrideTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape),
                    iconModifier = Modifier.size(28.dp)
                ) {
                    state.mapView?.let { it ->
                        it.location.updateSettings {
                            locationPuck = createDefault2DPuck(withBearing = true)
                            puckBearingEnabled = true
                            puckBearing = PuckBearing.HEADING
                            enabled = true
                        }
                    }
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
                    when (state.recordStatus) {
                        RecordViewModel.RecordStatus.NONE ->
                            TextButton(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clip(CircleShape)
                                    .background(StrideTheme.colorScheme.secondary, CircleShape)
                                    .size(85.dp),
                                onClick = {
                                    if (state.mapView != null) {
                                        val listener = OnIndicatorPositionChangedListener { point ->
                                            viewModel.startRecord(point)
                                        }
                                        state.mapView?.location?.addOnIndicatorPositionChangedListener(
                                            listener
                                        )
                                        state.mapView?.location?.removeOnIndicatorPositionChangedListener(
                                            listener
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
                                TextButton(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 8.dp)
                                        .clip(CircleShape)
                                        .background(StrideTheme.colorScheme.background, CircleShape)
                                        .size(85.dp),
                                    onClick = { viewModel.resume() }) {
                                    Text(
                                        "RESUME",
                                        color = StrideTheme.colorScheme.onBackground,
                                        style = StrideTheme.typography.titleMedium
                                    )
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
                                        onClick = { viewModel.finish() }) {
                                        Text(
                                            "FINISH",
                                            color = StrideTheme.colorScheme.onSecondary,
                                            style = StrideTheme.typography.titleMedium
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    val isVisibleMetric =
                                        state.screenStatus == RecordViewModel.ScreenStatus.DETAIL
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
                            IconButton(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .background(
                                        StrideTheme.colorScheme.surfaceContainerLowest,
                                        CircleShape
                                    )
                                    .clip(CircleShape)
                                    .size(85.dp),
                                onClick = { viewModel.stop() }) {
                                Icon(
                                    modifier = Modifier.size(33.dp),
                                    painter = painterResource(R.drawable.filled_round_square_icon),
                                    contentDescription = "Stop record",
                                    tint = StrideTheme.colorScheme.onBackground
                                )
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
        when (state.screenStatus) {
            RecordViewModel.ScreenStatus.DEFAULT -> {
                MapboxMap(
                    Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding()),
                    mapViewportState = mapViewportState,
                    style = { MapStyle(style = Style.MAPBOX_STREETS) }
                ) {
                    MapEffect(Unit) { mv ->
                        viewModel.setMapView(mv)

                        mv.location.addOnIndicatorPositionChangedListener { point ->
                            Log.d(
                                "MapboxLocation",
                                "Updated Location: ${point.longitude()}, ${point.latitude()}"
                            )

                            if (state.recordStatus == RecordViewModel.RecordStatus.RECORDING)
                                viewModel.addPoints(point)
                        }
                    }
                    if (state.startPoint != null) {
                        CircleAnnotation(point = state.startPoint!!) {
                            // Style the circle that will be added to the map.
                            circleRadius = 5.0
                            circleColor = StrideColor.green
                            circleStrokeWidth = 1.5
                            circleStrokeColor = Color(0xffffffff)
                        }
                    }
                }
            }

            RecordViewModel.ScreenStatus.DETAIL -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    RecordValueBlock(
                        title = "Time",
                        value = formatTimeByMillis(state.activityMetric.time)
                    )
                    RecordValueBlock(
                        type = RecordValueBlockType.Large,
                        title = "Avg Speed",
                        value = state.activityMetric.avgSpeed.toString()
                    )
                    RecordValueBlock(
                        title = "Distance",
                        value = state.activityMetric.distance.toString()
                    )
                }
            }

            RecordViewModel.ScreenStatus.SAVING -> {}

            RecordViewModel.ScreenStatus.SENSOR -> {}
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

@Composable
fun CurrentLocationButton(
    mapViewportState: MapViewportState,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    action: () -> Unit = {}
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "GPS on!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Can't access current location", Toast.LENGTH_SHORT).show()
        }
    }

    FloatingActionButton(
        modifier = modifier,
        onClick = {
            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setMinUpdateIntervalMillis(5000)
                    .setWaitForAccurateLocation(true)
                    .build()

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                .setAlwaysShow(true)

            val client: SettingsClient = LocationServices.getSettingsClient(context)

            client.checkLocationSettings(builder.build())
                .addOnSuccessListener(OnSuccessListener {
                    action()
                    mapViewportState.transitionToFollowPuckState(
                        completionListener = { isFinish ->
                            if (isFinish) {
                                mapViewportState.setCameraOptions { bearing(null) }
                            }
                        }
                    )
                })
                .addOnFailureListener(OnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            val intentSenderRequest =
                                IntentSenderRequest.Builder(exception.resolution).build()
                            launcher.launch(intentSenderRequest)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, "Can't check location", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }) {
        Icon(
            modifier = iconModifier,
            painter = painterResource(R.drawable.user_location_icon),
            contentDescription = "Focus to Location",
            tint = StrideTheme.colorScheme.onBackground
        )
    }
}
