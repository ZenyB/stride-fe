package com.trio.stride.ui.screens.activity.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.gson.JsonObject
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.util.isEmpty
import com.trio.stride.R
import com.trio.stride.domain.model.Activity
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.activity.detail.ActivityActionDropdown
import com.trio.stride.ui.components.activity.detail.ActivityDetailView
import com.trio.stride.ui.components.activity.detail.BottomSheetIndicator
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.components.map.mapstyle.MapStyleBottomSheet
import com.trio.stride.ui.screens.activity.detail.ActivityFormMode
import com.trio.stride.ui.screens.activity.detail.ActivityFormView
import com.trio.stride.ui.screens.maps.saveroute.SaveRouteForm
import com.trio.stride.ui.screens.maps.view.INITIAL_ZOOM
import com.trio.stride.ui.screens.maps.view.ZOOM_MORE
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    id: String = "",
    navController: NavController,
    viewModel: ActivityDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val item by viewModel.item.collectAsStateWithLifecycle()

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        sheetState
    )
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(106.80259579, 10.87007182))
            zoom(INITIAL_ZOOM)
            pitch(0.0)
        }
    }
    var polylineManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
    var mapStyle by remember { mutableStateOf(Style.MAPBOX_STREETS) }
    var showStyleSheet by remember { mutableStateOf(false) }
    var showDiscardEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val headerHeight =
        if (sheetState.currentValue != SheetValue.Expanded || sheetState.targetValue == SheetValue.PartiallyExpanded) {
            0.dp
        } else {
            WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 52.dp
        }
    val animatedSheetHeaderHeight by animateDpAsState(
        targetValue = headerHeight,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    val targetAlpha = if (sheetState.targetValue != SheetValue.Expanded) 0f else 1f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        label = "AnimatedAlpha"
    )

    fun drawRoute(points: List<Point>) {
        polylineManager?.create(
            PolylineAnnotationOptions()
                .withPoints(points)
                .withLineColor("#E90C56")
                .withLineWidth(5.0)
                .withData(JsonObject().apply {
                    addProperty("id", id)
                })
        )
    }

    LaunchedEffect(uiState) {
        if (uiState is ActivityDetailState.Deleted)
            navController.popBackStack()
    }

    LaunchedEffect(true) {
        viewModel.getActivityDetail(id)
    }

    LaunchedEffect(sheetState.targetValue) {
        Log.d("sheet state", "current: ${sheetState.currentValue}")
        Log.d("sheet state", "target: ${sheetState.targetValue}")
    }

    if (polylineManager != null) {
    }

    StrideDialog(
        visible = showDiscardEditDialog,
        title = "Discard Unsaved Change",
        subtitle = "Your changes will not be saved.",
        dismiss = { showDiscardEditDialog = false },
        destructiveText = "Discard",
        destructive = { viewModel.discardEdit() },
        dismissText = "Cancel"
    )

    StrideDialog(
        visible = showDeleteDialog,
        title = "Delete Activity",
        subtitle = "Your activity will be permanently deleted.",
        dismiss = { showDeleteDialog = false },
        destructiveText = "Delete",
        destructive = {
            showDeleteDialog = false
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            viewModel.deleteActivity()
        },
        dismissText = "Cancel"
    )

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {
                CustomLeftTopAppBar(
                    title = item?.sport?.name ?: "Activity",
                    titleModifier = Modifier.alpha(alpha = animatedAlpha),
                    backgroundColor = StrideTheme.colorScheme.surface.copy(alpha = animatedAlpha),
                    dividerColor = StrideTheme.colors.grayBorder.copy(alpha = animatedAlpha),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier
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
                    },
                    actions = {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (uiState is ActivityDetailState.Loading || uiState is ActivityDetailState.Error) {
                                LoadingSmall()
                            } else {
                                IconButton(
                                    onClick = {
                                        item?.let { viewModel.savingRoute() }
                                    },
                                    modifier = Modifier
                                        .background(
                                            color = StrideTheme.colors.white,
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_save),
                                        contentDescription = "Close Sheet"
                                    )
                                }
                                ActivityActionDropdown(
                                    handleDelete = { showDeleteDialog = true },
                                    handleEdit = { viewModel.openEditView() }
                                )
                            }
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { showStyleSheet = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = StrideTheme.colors.white.copy(alpha = 1 - animatedAlpha)
                        ),
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.CenterEnd)
                            .alpha(1 - animatedAlpha),
                        enabled = sheetState.currentValue != SheetValue.Expanded
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.layers_icon),
                            contentDescription = "Map option",
                            tint = StrideTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .alpha(1 - animatedAlpha)
                        )
                    }
                }
            }
        }
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 300.dp,
            sheetContainerColor = StrideTheme.colors.white,
            sheetContent = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding()
                            )
                            .padding(top = animatedSheetHeaderHeight),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (sheetState.currentValue != SheetValue.Expanded) {
                            BottomSheetIndicator(
                                color = StrideTheme.colorScheme.outline
                            )
                        }

                        when (uiState) {
                            is ActivityDetailState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingSmall()
                                }
                            }

                            is ActivityDetailState.Idle -> {
                                item?.let { ActivityDetailView(it) }
                            }

                            is ActivityDetailState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        (uiState as ActivityDetailState.Error).message,
                                        style = StrideTheme.typography.titleMedium,
                                        color = StrideTheme.colors.red700
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
            },
            sheetDragHandle = null,
        ) { padding ->
            MapboxMap(
                Modifier
                    .fillMaxSize(), mapViewportState = mapViewportState,
                style = { MapStyle(style = mapStyle) },
                scaleBar = {},
                compass = {}
            ) {
                MapEffect(Unit) { mapView ->
                    val annotationApi = mapView.annotations
                    polylineManager = annotationApi.createPolylineAnnotationManager(
                        annotationConfig = AnnotationConfig(
                            layerId = "touch-map-route"
                        ),
                    )
                    polylineManager?.lineJoin = LineJoin.ROUND
                    polylineManager?.lineCap = LineCap.ROUND
                }
                MapEffect(item) { mapView ->
                    if (item != null) {
                        val coords =
                            LineString.fromPolyline(item?.geometry ?: "", 5).coordinates()
                        if (coords.isNotEmpty()) {
                            drawRoute(coords)

                            val cameraOptions =
                                CameraOptions.Builder().center(coords[0]).build()
                            val options =
                                CameraOptions.Builder().build()
                            mapView.mapboxMap.cameraForCoordinates(
                                coords,
                                cameraOptions,
                                EdgeInsets(300.0, 300.0, 700.0, 300.0),
                                ZOOM_MORE,
                                null
                            ) { result ->
                                if (result.isEmpty) {
                                    //TODO: error
                                } else {
                                    mapViewportState.flyTo(result)
                                }
                            }
                        }
                    }
                }
            }

            if (showStyleSheet) {
                MapStyleBottomSheet(
                    mapStyle = mapStyle,
                    onMapStyleSelected = { style ->
                        mapStyle = style
                    },
                    onDismiss = { showStyleSheet = false }
                )
            }
        }
    }

    AnimatedVisibility(
        uiState == ActivityDetailState.Edit,
        enter = slideInVertically(
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }
        )
    ) {
        ActivityFormView(
            "Edit Activity",
            "DONE",
            mode = ActivityFormMode.Update(
                activity = if (item != null) {
                    Activity(
                        id = item!!.id,
                        mapImage = item!!.mapImage,
                        images = item!!.images.map { it.toString() },
                        name = item!!.name,
                        description = item!!.description,
                        sport = item!!.sport,
                        rpe = item!!.rpe.toInt(),
                    )
                } else Activity(),
                onUpdate = { dto, sport ->
                    viewModel.updateActivity(dto, sport, {
                        navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
                    })
                },
                onDiscard = {
                    showDiscardEditDialog = true
                }
            ),
            dismissAction = { showDiscardEditDialog = true },
            isSaving = uiState == ActivityDetailState.Loading,
        )
    }

    if (item?.routeId != null) {
        BackHandler(enabled = uiState == ActivityDetailState.SavingRoute) {
            viewModel.discardEdit()
        }
        item!!.routeId?.let {
            SaveRouteForm(
                visible = uiState == ActivityDetailState.SavingRoute,
                routeId = it,
                sport = item!!.sport,
                mapImage = item!!.mapImage,
                distance = item!!.totalDistance ?: 0.0,
                onFinish = { viewModel.discardEdit() },
            )
        }
    }
}

