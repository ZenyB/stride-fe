package com.trio.stride.ui.screens.maps.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.gson.JsonObject
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.LayerPosition
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.withLineColor
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.addLayerBelow
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.trio.stride.R
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.map.MapStyleBottomSheet
import com.trio.stride.ui.screens.login.LoginViewModel
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.map.CityLocations
import com.trio.stride.ui.utils.map.RequestLocationPermission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val ZOOM = 16.0
const val ROAD_LABEL = "road-label"
const val ROAD_SIMPLE_LABEL = "road-label-simple"


@Composable
fun ViewMapScreen(
    navController: NavController,
    mapStyleViewModel: MapStyleViewModel = hiltViewModel()
) {
    val mapboxAccessToken = stringResource(id = R.string.mapbox_access_token)
    val mapStyle by mapStyleViewModel.mapStyle.collectAsStateWithLifecycle()

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
    var showSheet by remember { mutableStateOf(false) }


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

    val client = MapboxDirections.builder()
        .accessToken(mapboxAccessToken)

    val drawnRoutes = mutableMapOf<String, PolylineAnnotation?>()

    var currentIndex = 0
    var selectedIndex by remember { mutableIntStateOf(0) }


    val allRoutes = mutableMapOf<String, List<Point>>()
    var touchManager: PolylineAnnotationManager? = null
    var polylineAnnotationManager: PolylineAnnotationManager? = null
    var selectedRouteManager: PolylineAnnotationManager? = null


    fun drawRoute(id: String, points: List<Point>) {
        val isSelected = id == selectedIndex.toString()
        val manager =
            if (isSelected) selectedRouteManager else polylineAnnotationManager

        val polyline = manager?.create(
            PolylineAnnotationOptions()
                .withPoints(points)
                .withData(JsonObject().apply {
                    addProperty("id", id)
                })
        )

        touchManager?.create(
            PolylineAnnotationOptions()
                .withPoints(points)
                .withLineColor("#FFFFFF")
                .withLineWidth(24.0)
                .withLineOpacity(0.0)
                .withData(JsonObject().apply {
                    addProperty("id", id)
                })


        )
        drawnRoutes[id] = polyline
        allRoutes[id] = points
    }

    fun fetchAndDrawAllRoutes(annotationManager: PolylineAnnotationManager) {
        if (allRoutes.isEmpty()) {
            CityLocations.SamplePoints.forEachIndexed { index, routeData ->
                val routeOptions = RouteOptions.builder()
                    .coordinatesList(listOf(routeData.first, routeData.second))
                    .alternatives(true)
                    .applyDefaultNavigationOptions()
                    .build()
                val request = client
                    .routeOptions(routeOptions)
                    .build()

                request.enqueueCall(object : Callback<DirectionsResponse> {
                    override fun onResponse(
                        call: Call<DirectionsResponse>,
                        response: Response<DirectionsResponse>
                    ) {
                        val route = response.body()?.routes()?.firstOrNull() ?: return
                        val coords =
                            LineString.fromPolyline(route.geometry() ?: "", 6).coordinates()
                        drawRoute(currentIndex.toString(), coords)
                        currentIndex++

                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                        Log.e("handle tap", "Failed to fetch route: ${t.message}")
                    }
                })
            }

        }
    }
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

    Scaffold(
        floatingActionButton = {
            Column(modifier = Modifier.offset(y = ((-100).dp))) {
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

                Spacer(modifier = Modifier.height(12.dp))
                FloatingActionButton(
                    onClick = { showSheet = true },
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Options")
                }
            }


        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
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
                style = { MapStyle(style = mapStyle) }
            ) {
                MapEffect(Unit) { mapView ->

                    val annotationApi = mapView.annotations
                    selectedRouteManager = annotationApi.createPolylineAnnotationManager(
                        annotationConfig = AnnotationConfig(
                            belowLayerId = ROAD_LABEL,
                            layerId = "selected-map-route"
                        ),
                    )
                    selectedRouteManager?.lineColorString = "#E90C56"
                    selectedRouteManager?.lineWidth = 5.0
                    selectedRouteManager?.lineJoin = LineJoin.ROUND
                    selectedRouteManager?.lineCap = LineCap.ROUND

                    polylineAnnotationManager =
                        annotationApi.createPolylineAnnotationManager(
                            annotationConfig = AnnotationConfig(
                                belowLayerId = "selected-map-route",
                                layerId = "view-map-route"
                            ),
                        )
                    polylineAnnotationManager?.lineColorString = "#FC869D"
                    polylineAnnotationManager?.lineWidth = 5.0
                    polylineAnnotationManager?.lineJoin = LineJoin.ROUND
                    polylineAnnotationManager?.lineCap = LineCap.ROUND
                    touchManager = annotationApi.createPolylineAnnotationManager(
                        annotationConfig = AnnotationConfig(
                            layerId = "touch-map-route"
                        ),
                    )

                    touchManager!!.addClickListener { clickedAnnotation ->
                        val clickedId =
                            clickedAnnotation.getData()?.asJsonObject?.get("id")?.asString
                        Log.d("handle tap", "touch id: $clickedId")

                        clickedId?.let {
                            if (selectedIndex != it.toInt()) {
                                drawnRoutes[selectedIndex.toString()]?.let { polyline ->
                                    val newPolyline = polylineAnnotationManager?.create(
                                        PolylineAnnotationOptions()
                                            .withPoints(polyline.points)
                                            .withData(JsonObject().apply {
                                                addProperty("id", it)
                                            })
                                    )
                                    selectedRouteManager?.delete(polyline)
                                    drawnRoutes[selectedIndex.toString()] = newPolyline
                                }

                                drawnRoutes[it]?.let { polyline ->
                                    val newPolyline = selectedRouteManager?.create(
                                        PolylineAnnotationOptions()
                                            .withPoints(polyline.points)
                                            .withData(JsonObject().apply {
                                                addProperty("id", it)
                                            })
                                    )
                                    polylineAnnotationManager?.delete(polyline)
                                    drawnRoutes[it] = newPolyline
                                    Log.d("handle tap", "change color darker")
                                }
                                selectedIndex = it.toInt()
                            }
                        }
                        true
                    }

                    mapView.mapboxMap.subscribeStyleLoaded {
                        mapView.mapboxMap.style?.moveStyleLayer(
                            ROAD_LABEL,
                            LayerPosition("selected-map-route", null, null)
                        )
                        mapView.mapboxMap.style?.moveStyleLayer("touch-map-route", null)


                        mapView.mapboxMap.style?.styleLayers?.forEachIndexed { index, layer ->
                            Log.d("MapLayers", "Layer $index: ${layer.id}")
                        }

                    }
                    fetchAndDrawAllRoutes(polylineAnnotationManager!!)



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
                style = StrideTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .padding(end = 16.dp, bottom = 100.dp)
        ) {

        }

        if (showSheet) {
            MapStyleBottomSheet(
                mapStyle = mapStyle,
                onMapStyleSelected = { mapStyleViewModel.selectStyle(it) },
                onDismiss = { showSheet = false }
            )
        }
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
