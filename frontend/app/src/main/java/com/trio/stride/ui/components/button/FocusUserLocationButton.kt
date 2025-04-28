package com.trio.stride.ui.components.button

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.data.ViewportStatusChangeReason
import com.mapbox.maps.plugin.viewport.viewport
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.map.BearingStatus
import com.trio.stride.ui.utils.map.checkLocationOn
import com.trio.stride.ui.utils.map.isLocationEnabled

@Composable
fun FocusUserLocationButton(
    mapView: MapView,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
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

    val followPuckWithBearing by remember {
        mutableStateOf(
            mapView.viewport.makeFollowPuckViewportState(
                FollowPuckViewportStateOptions.Builder()
                    .bearing(FollowPuckViewportStateBearing.SyncWithLocationPuck)
                    .pitch(0.6)
                    .zoom(16.0)
                    .build()
            )
        )
    }

    val followPuckNoBearing by remember {
        mutableStateOf(
            mapView.viewport.makeFollowPuckViewportState(
                FollowPuckViewportStateOptions.Builder()
                    .bearing(FollowPuckViewportStateBearing.Constant(0.0))
                    .pitch(0.0)
                    .zoom(16.0)
                    .build()
            )
        )
    }

    val bearingStatus = remember { mutableStateOf(BearingStatus.NO_GPS) }

    val observer =
        { fromStatus: ViewportStatus, toStatus: ViewportStatus, reason: ViewportStatusChangeReason ->
            if (toStatus is ViewportStatus.State) {
                bearingStatus.value = when (toStatus.state) {
                    followPuckWithBearing -> BearingStatus.HEADING
                    followPuckNoBearing -> BearingStatus.FOCUS
                    else -> BearingStatus.NONE
                }
            } else if (toStatus is ViewportStatus.Idle)
                bearingStatus.value = BearingStatus.NONE
        }

    LaunchedEffect(Unit) {
        mapView.viewport.addStatusObserver(observer)
    }

    DisposableEffect(Unit) {
        mapView.viewport.addStatusObserver(observer)
        onDispose {
            mapView.viewport.removeStatusObserver(observer)
        }
    }

    FloatingActionButton(
        modifier = modifier
            .size(44.dp)
            .background(StrideTheme.colorScheme.background, CircleShape)
            .clip(CircleShape),
        containerColor = StrideTheme.colorScheme.background,
        shape = CircleShape,
        onClick = {
            if (isLocationEnabled(context)) {
                val viewportStatus = mapView.viewport.status

                if (viewportStatus is ViewportStatus.State) {
                    when (viewportStatus.state) {
                        followPuckWithBearing -> {
                            mapView.viewport.transitionTo(followPuckNoBearing)
                            bearingStatus.value = BearingStatus.FOCUS
                        }

                        followPuckNoBearing -> {
                            mapView.viewport.transitionTo(followPuckWithBearing)
                            bearingStatus.value = BearingStatus.HEADING
                        }

                        else -> {
                            mapView.viewport.transitionTo(followPuckNoBearing)
                        }
                    }
                } else {
                    mapView.viewport.transitionTo(followPuckNoBearing)
                }
            } else {
                checkLocationOn(context, mapView, launcher)
            }
        }
    ) {
        Crossfade(targetState = bearingStatus.value, label = "Bearing Icon") { status ->
            val icon = when (status) {
                BearingStatus.NONE -> painterResource(R.drawable.user_location_icon)
                BearingStatus.HEADING -> painterResource(R.drawable.compass_icon)
                BearingStatus.FOCUS -> painterResource(R.drawable.gps_focus_icon)
                BearingStatus.NO_GPS -> painterResource(R.drawable.gps_off_icon)
            }

            Icon(
                modifier = iconModifier.size(28.dp),
                painter = icon,
                contentDescription = "User location icon button",
                tint = if (status != BearingStatus.FOCUS && status != BearingStatus.HEADING) StrideTheme.colorScheme.onBackground else StrideTheme.colorScheme.primary
            )
        }
    }
}
