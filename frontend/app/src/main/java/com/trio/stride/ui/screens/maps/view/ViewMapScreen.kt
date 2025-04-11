package com.trio.stride.ui.screens.maps.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.geojson.GeometryCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.trio.stride.R
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.map.CityLocations
import com.trio.stride.ui.utils.map.RequestLocationPermission

const val ZOOM = 16.0

@Composable
fun ViewMapScreen(
    navController: NavController,
) {
    val selectedPoint = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Point>("selected_point")
        ?.observeAsState()

    val context = LocalContext.current
    var permissionRequestCount by remember {
        mutableIntStateOf(0)
    }
    var isMapAvailable by remember {
        mutableStateOf(false)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(106.80259579, 10.87007182))
            zoom(ZOOM)
            pitch(0.0)
        }
    }

    val followOptions = FollowPuckViewportStateOptions.Builder()
        .pitch(0.0)
        .zoom(ZOOM)
        .build()


    LaunchedEffect(true) {
        if (selectedPoint?.value == null) {
            mapViewportState.transitionToFollowPuckState(
                followOptions,
                completionListener = { isFinish ->
                    if (isFinish) {
                        mapViewportState.setCameraOptions { bearing(null) }
                    }
                })
        }
    }

    Scaffold(floatingActionButton = {
        if (mapViewportState.mapViewportStatus == ViewportStatus.Idle) {
            FloatingActionButton(
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape,
                onClick = {
                    mapViewportState.transitionToFollowPuckState(
                        followOptions,
                        completionListener = { isFinish ->
                            if (isFinish) {
                                mapViewportState.setCameraOptions {
                                    bearing(null)
                                    zoom(ZOOM)
                                    pitch(0.0)
                                }
                            }
                        })
                }
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                    contentDescription = "Locate button",
                    tint = Color.Black
                )
            }
        }
    }) { padding ->
        RequestLocationPermission(
            requestCount = permissionRequestCount,
            onPermissionDenied = {
                isMapAvailable = false
            },
            onPermissionReady = {
                isMapAvailable = true
            }
        )
        if (isMapAvailable) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                style = { MapStyle(style = Style.MAPBOX_STREETS) }

            ) {
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                        enabled = true
                    }
                    if (selectedPoint?.value != null) {
                        val cameraOptions =
                            CameraOptions.Builder().center(selectedPoint.value).build()

                        mapView.mapboxMap.flyTo(cameraOptions)
                    }

                }

                if (selectedPoint != null) {
                    selectedPoint.value?.let {
                        CircleAnnotation(point = it) {
                            circleRadius = 8.0
                            circleColor = StrideColor.green600
                            circleStrokeWidth = 2.0
                            circleStrokeColor = StrideColor.background
                        }
                    }

                }
            }
        }

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .clickable { navController.navigate(Screen.BottomNavScreen.Search.route) }
        ) {
            Text(
                "Search locations",
                color = StrideColor.gray,
                style = StrideTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }


        MapFallbackScreen(
            isMapAvailable,
            permissionRequestCount,
            onRetry = { permissionRequestCount += 1 },
            goToSetting = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            })
    }
}


@Composable
fun MapFallbackScreen(
    isMapAvailable: Boolean,
    permissionRequestCount: Int,
    onRetry: () -> Unit,
    goToSetting: () -> Unit
) {
    if (!isMapAvailable) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onRetry
                ) {
                    Text("Try again")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = goToSetting
                ) {
                    Text("Go to Settings")
                }
            }
        }
    }
}
