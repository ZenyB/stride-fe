package com.trio.stride.ui.screens.maps.saveroutedetail

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.ui.components.button.userlocation.FocusUserLocationButton
import com.trio.stride.ui.components.map.routesheet.RouteItemDetail
import com.trio.stride.ui.screens.maps.view.INITIAL_ZOOM
import com.trio.stride.ui.screens.maps.view.ZOOM_MORE
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveRouteDetailScreen(
    item: RouteItem,
    startRecord: (String) -> Unit,
    onBack: (isDeleted: Boolean?) -> Unit,
    viewModel: SaveRouteDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mapView by viewModel.mapView.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


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

    val peekHeight =
        WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() + 72.dp + 88.dp + 16.dp

    var polylineManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }

    fun drawRoute(points: List<Point>) {
        polylineManager?.create(
            PolylineAnnotationOptions()
                .withPoints(points)
                .withLineColor("#E90C56")
                .withLineWidth(5.0)

        )
    }

    when (uiState) {
        is ViewRouteDetailState.Error -> Toast.makeText(
            context,
            (uiState as ViewRouteDetailState.Error).message,
            Toast.LENGTH_SHORT
        ).show()

        ViewRouteDetailState.Loading -> Toast.makeText(
            context,
            "Deleting saved route...",
            Toast.LENGTH_SHORT
        ).show()

        ViewRouteDetailState.Success -> {
            Toast.makeText(
                context,
                "Delete saved route successfully!",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetState()
            onBack(true)
        }

        else -> {

        }
    }


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
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
                    BackHandler(enabled = true) {
                        onBack(false)
                    }
                    RouteItemDetail(
                        item,
                        isSaving = uiState==ViewRouteDetailState.Loading,
                        isSaved = true,
                        onSaveRoute = {
                            viewModel.deleteSavedRoute(item.id)
                        },
                        startRecord = { geometry ->
                            viewModel.setCurrentSport(item.sportId)
                            startRecord(geometry)
                        })
                }
                IconButton(
                    onClick = {
                        onBack(false)
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

        },
    ) { padding ->
        MapboxMap(
            Modifier
                .fillMaxSize(), mapViewportState = mapViewportState,
            style = { MapStyle(style = Style.MAPBOX_STREETS) },
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
                val coords =
                    LineString.fromPolyline(item.geometry, 5).coordinates()
                if (coords.isNotEmpty()) {
                    drawRoute(coords)

                    val cameraOptions =
                        CameraOptions.Builder().center(coords[0]).build()
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
                    mapView?.let {
                        FocusUserLocationButton(mapView = it)
                    }
                }
            }
        }
    }
}