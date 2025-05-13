package com.trio.stride.ui.screens.maps.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.LayerPosition
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.util.isEmpty
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.trio.stride.R
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.button.userlocation.FocusUserLocationButton
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.components.map.MapFallbackScreen
import com.trio.stride.ui.components.map.SearchFieldWithButton
import com.trio.stride.ui.components.map.mapstyle.MapStyleBottomSheet
import com.trio.stride.ui.components.map.mapstyle.MapStyleViewModel
import com.trio.stride.ui.components.map.routesheet.RouteItemDetail
import com.trio.stride.ui.components.map.routesheet.RouteList
import com.trio.stride.ui.components.map.routesheet.RoutePager
import com.trio.stride.ui.components.sport.bottomsheet.SportMapBottomSheet
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportInSearch
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportInSearchViewModel
import com.trio.stride.ui.screens.maps.saveroute.SaveRouteState
import com.trio.stride.ui.screens.maps.saveroute.SaveRouteViewModel
import com.trio.stride.ui.theme.StrideColor
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.map.LocationUtils
import com.trio.stride.ui.utils.map.RequestLocationPermission
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

const val ZOOM = 11.0
const val ZOOM_MORE = 14.0
const val INITIAL_ZOOM = 9.0
const val ROAD_LABEL = "road-label"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewMapScreen(
    navController: NavController,
    mapStyleViewModel: MapStyleViewModel = hiltViewModel(),
    searchSportViewModel: ChooseSportInSearchViewModel = hiltViewModel(),
    saveRouteViewModel: SaveRouteViewModel = hiltViewModel(),
    viewModel: ViewMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mapStyle by mapStyleViewModel.mapStyle.collectAsStateWithLifecycle()
    val mapView by viewModel.mapView.collectAsStateWithLifecycle()

    val selectedSport by searchSportViewModel.selectedSport.collectAsStateWithLifecycle()
    val sportsList by searchSportViewModel.sportsList.collectAsStateWithLifecycle()
    var showSportSheet by remember { mutableStateOf(false) }


    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val selectedPoint = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Point>("selected_point")
        ?.observeAsState()
    var currentLocation: Point = Point.fromLngLat(106.80259579, 10.87007182)

    val routeItems by viewModel.routeItems.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val saveRouteState by saveRouteViewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        sheetState
    )
    val pagerState = rememberPagerState(pageCount = { routeItems.size })

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
            zoom(INITIAL_ZOOM)
            pitch(0.0)
        }
    }

    val followOptions = FollowPuckViewportStateOptions.Builder()
        .pitch(0.0)
        .zoom(ZOOM)
        .build()

    var selectedIndex by remember { mutableIntStateOf(0) }
    val peekHeight = if (uiState is ViewMapState.ViewRouteDetail) {
        400.dp
    } else {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 72.dp + 88.dp
    }
    val animatedPeekHeight by animateDpAsState(
        targetValue = peekHeight,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )

    var touchManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
    var polylineAnnotationManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
    var selectedRouteManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
    var circleAnnotationManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }


    val coroutineScope = rememberCoroutineScope()
    val isScrolling = remember { mutableStateOf(false) }

    fun scrollPager(index: Int) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(index)
        }
    }

    fun drawRoute(id: String, points: List<Point>) {
        if (id == selectedIndex.toString()) {
            selectedRouteManager?.create(
                PolylineAnnotationOptions()
                    .withPoints(points)
                    .withData(JsonObject().apply {
                        addProperty("id", id)
                    })
            )
        }
        val polyline = polylineAnnotationManager?.create(
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
        viewModel.setDrawnRoute(id.toInt(), polyline)
        viewModel.addRoute(id, points)

        val searchPoint = selectedPoint?.value ?: currentLocation
        val listPoints = points.toMutableList()
        listPoints.add(searchPoint)
        val options =
            CameraOptions.Builder().center(searchPoint).build()
        mapView?.mapboxMap?.cameraForCoordinates(
            listPoints,
            options, EdgeInsets(500.0, 300.0, 700.0, 300.0), ZOOM, null
        ) { result ->
            if (result.isEmpty) {
                //TODO: error
            } else {
                mapViewportState.flyTo(result)
            }
        }
        Log.d("map route", "map route $id")
    }

    fun clearRoute() {
        Log.d("map route", "clearing routes")
        touchManager?.deleteAll()
        polylineAnnotationManager?.deleteAll()
        selectedRouteManager?.deleteAll()
    }

    fun selectRoute(index: Int) {
        Log.d("handle tap", "new index $index")
        Log.d("handle tap", "selected index $selectedIndex")

        if (selectedIndex != index) {
            Log.d("handle tap", "drawn route ${viewModel.getDrawnRoute(index)}")

            viewModel.getDrawnRoute(index)?.let { polyline ->
                selectedRouteManager?.deleteAll()
                Log.d("handle tap", "polyline $polyline")

                selectedRouteManager?.create(
                    PolylineAnnotationOptions()
                        .withPoints(polyline.points)
                        .withData(JsonObject().apply {
                            addProperty("id", index)
                        })
                )
                Log.d("handle tap", "manager $selectedRouteManager")

                Log.d("handle tap", "change color darker")
            }
            selectedIndex = index
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.targetPage }
            .distinctUntilChanged()
            .collect { page ->
                selectRoute(pagerState.targetPage)
            }
    }

    when (saveRouteState) {
        is SaveRouteState.ErrorSaving -> {
            StrideDialog(
                visible = true,
                title = "Error saving activity",
                description = (saveRouteState as SaveRouteState.ErrorSaving).message,
                dismiss = { saveRouteViewModel.resetState() },
                dismissText = "OK",
            )
        }

        is SaveRouteState.Success -> {
            StrideDialog(
                visible = true,
                title = "Route Saved",
                description = "You can access your Saved Routes from Maps and your profile",
                dismiss = { saveRouteViewModel.resetState() },
                dismissText = "OK",
            )
        }

        is SaveRouteState.IsSaving -> {
            Loading()
        }

        else -> {

        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = animatedPeekHeight,
        sheetContainerColor = StrideTheme.colors.white,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 96.dp)
                        .padding(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (uiState) {
                        is ViewMapState.GetRouteError -> {
                            Text(
                                (uiState as ViewMapState.GetRouteError).message,
                                style = StrideTheme.typography.titleMedium
                            )
                        }

                        is ViewMapState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = StrideTheme.colorScheme.primary,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp
                            )
                        }

                        is ViewMapState.ViewRouteDetail -> {
                            BackHandler(enabled = true) {
                                viewModel.backToNormalView()
                            }
                            RouteItemDetail(
                                routeItems[viewModel.currentDetailIndex],
                                onSaveRoute = {
                                    saveRouteViewModel.saveRoute(routeItems[viewModel.currentDetailIndex].id)
                                })
                        }

                        else -> {
                            val size = routeItems.size
                            if (size == 0) {
                                Text(
                                    "No route found",
                                    style = StrideTheme.typography.titleMedium
                                )
                            } else {
                                Text(
                                    "$size route${if (size > 1) "s" else ""}",
                                    style = StrideTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(24.dp))
                                RouteList(items = routeItems, onClick = { idx ->
                                    Log.d("click item", "item idx: ${idx}")
                                    viewModel.onRouteItemClick(idx)
                                })
                            }
                        }
                    }
                }
                when (uiState) {
                    is ViewMapState.ViewRouteDetail -> {
                        IconButton(
                            onClick = {
                                viewModel.backToNormalView()
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 8.dp, y = (-50).dp)
                                .background(
                                    color = StrideTheme.colors.white,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Close Sheet"
                            )
                        }
                    }

                    else -> {

                    }
                }

            }

        },
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
            LaunchedEffect(Unit, selectedSport) {
                if (selectedPoint?.value == null) {
                    mapViewportState.transitionToFollowPuckState(
                        followOptions,
                        completionListener = { isFinish ->
                            if (isFinish) {
                                mapViewportState.setCameraOptions { bearing(null) }
                            }
                        })
                    LocationUtils.getCurrentLocation(fusedLocationClient, context) { point ->
                        selectedSport?.let {
                            clearRoute()
                            viewModel.getRecommendRoute(point, it)
                        }
                        currentLocation = point
                    }
                } else {
                    selectedSport?.let {
                        clearRoute()
                        viewModel.getRecommendRoute(selectedPoint.value, it)
                    }
                }
            }
            MapboxMap(
                Modifier
                    .fillMaxSize(), mapViewportState = mapViewportState,
                style = { MapStyle(style = mapStyle) },
                scaleBar = {},
                compass = {}
            ) {
                MapEffect(Unit) { mapView ->
                    viewModel.setMapView(mapView)
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

                    touchManager?.addClickListener { clickedAnnotation ->
                        val clickedId =
                            clickedAnnotation.getData()?.asJsonObject?.get("id")?.asString
                        Log.d("handle tap", "touch id: $clickedId")

                        clickedId?.let {
                            isScrolling.value = true
                            scrollPager(it.toInt())
                        }
                        true
                    }
                    circleAnnotationManager = annotationApi.createPolylineAnnotationManager()

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

                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                        enabled = true
                    }
                }

                MapEffect(routeItems) {
                    var currentIndex = 0
                    routeItems.forEachIndexed { _, item ->
                        val coords =
                            LineString.fromPolyline(item.geometry ?: "", 5).coordinates()
                        drawRoute(currentIndex.toString(), coords)
                        currentIndex++
                    }
                }

                if (selectedPoint != null) {
                    selectedPoint.value?.let {
                        ViewAnnotation(
                            options = viewAnnotationOptions {
                                geometry(it)
                                allowOverlap(true)
                                allowOverlapWithPuck(true)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(StrideColor.green600, shape = CircleShape)
                                    .border(2.dp, StrideColor.white, shape = CircleShape)
                            )
                        }
                    }
                }
            }
        }

        SearchFieldWithButton(onSearchClick = {
            navController.navigate(Screen.BottomNavScreen.Search.route)
        }, onSaveButtonClick = {
            navController.navigate(Screen.SaveRouteScreen.route)
        }) {
            selectedSport?.let {
                ChooseSportInSearch(
                    it.image,
                    onClick = {
                        showSportSheet = true
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                )
                .padding(bottom = 72.dp)
                .padding(bottom = 96.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    mapView?.let { FocusUserLocationButton(mapView = it) }
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
                            tint = Color.Black,
                        )
                    }
                }

                if (routeItems.isNotEmpty()) {
                    RoutePager(
                        state = pagerState,
                        routeItems,
                        modifier = Modifier,
                        onClick = { idx ->
                            viewModel.onRouteItemClick(idx)
                        }
                    )
                }
            }

        }

        if (showSheet) {
            MapStyleBottomSheet(
                mapStyle = mapStyle,
                onMapStyleSelected = { mapStyleViewModel.selectStyle(it) },
                onDismiss = { showSheet = false }
            )
        }

        SportMapBottomSheet(
            sports = sportsList,
            selectedSport = selectedSport,
            onItemClick = {
                searchSportViewModel.selectSport(it)
                showSportSheet = false
            },
            dismissAction = { showSportSheet = false },
            visible = showSportSheet
        )

        if (!isMapAvailable) {
            MapFallbackScreen(
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
}