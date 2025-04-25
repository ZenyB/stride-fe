package com.trio.stride.ui.utils.map

import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport

enum class BearingStatus {
    NONE, // Not focus user location
    HEADING,
    FOCUS,
    NO_GPS
}

val followPuckWithBearingBuilder =
    FollowPuckViewportStateOptions.Builder()
        .bearing(FollowPuckViewportStateBearing.SyncWithLocationPuck)
        .pitch(0.6)
        .zoom(16.0)
        .build()

val followPuckWithoutBearingBuilder =
    FollowPuckViewportStateOptions.Builder()
        .bearing(FollowPuckViewportStateBearing.Constant(0.0))
        .pitch(0.0)
        .zoom(16.0)
        .build()

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun checkLocationOn(
    context: Context,
    mapView: MapView?,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    successAction: () -> Unit = {}
) {

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
            focusToUser(mapView)
            successAction()
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
}

fun focusToUser(
    mapView: MapView?,
    followUserHeading: Boolean = false
) {
    val viewportState = if (followUserHeading)
        mapView?.viewport?.makeFollowPuckViewportState(followPuckWithBearingBuilder)
    else
        mapView?.viewport?.makeFollowPuckViewportState(followPuckWithoutBearingBuilder)

    viewportState?.let {
        mapView?.viewport?.transitionTo(it)
    }
}