package com.trio.stride.ui.screens.record

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.trio.stride.R
import com.trio.stride.ui.components.CustomCenterTopAppBar
import com.trio.stride.ui.screens.maps.view.ZOOM
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.map.RequestLocationPermission
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordScreen() {
    val context = LocalContext.current
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(106.80259579, 10.87007182))
            zoom(ZOOM)
            pitch(0.0)
        }
    }
    var mapBoxMap by remember { mutableStateOf<MapboxMap?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var permissionRequestCount by remember {
        mutableStateOf(1)
    }
    var showMap by remember {
        mutableStateOf(false)
    }
    var isMapAvailable by remember {
        mutableStateOf(false)
    }
    var showRequestPermissionButton by remember {
        mutableStateOf(false)
    }
    var startPoint by remember { mutableStateOf<Point?>(null) }
    var isRecordStart by remember {
        mutableStateOf(false)
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    //
    // Hardcoded route coordinates
    val routeCoordinates = listOf(
        Point.fromLngLat(106.804094, 10.868982),
        Point.fromLngLat(106.804011, 10.868883),
        Point.fromLngLat(106.803827, 10.86924),
        Point.fromLngLat(106.803837, 10.869643),
        Point.fromLngLat(106.803828, 10.869744),
        Point.fromLngLat(106.803746, 10.869803),
        Point.fromLngLat(106.803331, 10.869871),
        Point.fromLngLat(106.802935, 10.86989),
        Point.fromLngLat(106.802685, 10.86999)
    )

    val locationPoints = remember { mutableStateListOf<Point>() }

    Scaffold(
        topBar = {
            CustomCenterTopAppBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                title = "Run",
                navigationIcon = {
                    TextButton(onClick = {}) {
                        Text(
                            "Close",
                            style = StrideTheme.typography.labelMedium,
                            color = StrideTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.record),
                            contentDescription = "Setting"
                        )
                    }
                })
        },
        floatingActionButton = {
            CurrentLocationButton(mapViewportState) {
                Log.i("MAPVIEWWWW", mapView.toString())
                mapView?.let { it ->
                    isRecordStart = true
                    it.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                        enabled = true
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxWidth()
                    .height(52.dp), Alignment.Center
            ) {
                if (!isRecordStart)
                    TextButton(
                        modifier = Modifier.padding(vertical = 8.dp),
                        onClick = { isRecordStart = true }) {
                        Text("START")
                    }
                else
                    TextButton(
                        modifier = Modifier.padding(vertical = 8.dp),
                        onClick = { isRecordStart = false }) {
                        Text("STOP")
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
        com.mapbox.maps.extension.compose.MapboxMap(
            Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding()),
            mapViewportState = mapViewportState,
            style = { MapStyle(style = Style.MAPBOX_STREETS) }
        ) {
            MapEffect(Unit) { mv ->
                mapView = mv
                drawRoute(mv, routeCoordinates)

                //
                mv.location.addOnIndicatorPositionChangedListener { point ->
                    Log.d(
                        "MapboxLocation",
                        "Updated Location: ${point.longitude()}, ${point.latitude()}"
                    )

                    if (locationPoints.isEmpty() && isRecordStart) {
                        startPoint = point
                    } else if (!isRecordStart && locationPoints.isNotEmpty()) {
                        drawRoute(mv, locationPoints)
                        locationPoints.clear()
                    } else if (!isRecordStart)
                        locationPoints.clear()

                    if (shouldAddPoint(point, locationPoints)) {
                        locationPoints.add(point)
                        updatePolyline(mv, locationPoints)
                    }
                }
            }
            if (startPoint != null) {
                val color = StrideTheme.colorScheme.primary
                CircleAnnotation(point = startPoint!!) {
                    // Style the circle that will be added to the map.
                    circleRadius = 5.0
                    circleColor = color
                    circleStrokeWidth = 1.5
                    circleStrokeColor = Color(0xffffffff)
                }
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

@Composable
fun CurrentLocationButton(mapViewportState: MapViewportState, action: () -> Unit = {}) {
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
        modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 52.dp),
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
        Icon(Icons.Filled.LocationOn, contentDescription = "Focus to Location")
    }
}

private fun updatePolyline(mapView: MapView, points: List<Point>) {
    if (points.size < 2) return
    val lineString = LineString.fromLngLats(points)

    val map = mapView.mapboxMap


    map.getStyle { style ->
        val sourceId = "live-route-source"
        val layerId = "live-route-layer"

        // Update existing source or create a new one
        val source = style.getSourceAs<GeoJsonSource>(sourceId)
        if (source != null) {
            source.geometry(lineString)
        } else {
            val newSource = geoJsonSource(sourceId) {
                geometry(lineString)
            }
            style.addSource(newSource)

            style.addLayerAbove(
                lineLayer(layerId, sourceId) {
                    lineColor("#2571db") // Blue line
                    lineWidth(4.0)
                },
                "default-route-layer"
            )
        }
    }
}

private fun shouldAddPoint(
    newPoint: Point,
    points: List<Point>,
    minDistance: Double = 2.5
): Boolean {
    if (points.isEmpty()) return true

    val lastPoint = points.last()
    val distance = haversineDistance(
        lastPoint.latitude(),
        lastPoint.longitude(),
        newPoint.latitude(),
        newPoint.longitude()
    ) // Calculate distance in meters

    return distance >= minDistance
}

fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c // Distance in meters
}

fun drawRoute(mapView: MapView, routeCoordinates: List<Point>) {
    val mapboxMap = mapView.mapboxMap
    val lineString = LineString.fromLngLats(routeCoordinates)

    mapboxMap.getStyle { style ->
        val sourceId = "default-route-source"
        val layerId = "default-route-layer"
        val source = geoJsonSource(sourceId) {
            geometry(lineString)
        }
        style.addSource(source)

        val layer = lineLayer(layerId, sourceId) {
            lineColor("#e01659")
            lineWidth(4.0)
        }
        style.addLayer(layer)
    }
}
