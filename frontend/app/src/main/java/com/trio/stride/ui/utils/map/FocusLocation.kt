package com.trio.stride.ui.utils.map

import android.content.Context
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
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

fun checkLocationOn(
    context: Context,
    mapViewportState: MapViewportState,
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
            focusToUser(mapView, mapViewportState)
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
    mapViewportState: MapViewportState
) {
    mapView?.let {
        it.location.updateSettings {
            locationPuck = createDefault2DPuck(withBearing = true)
            puckBearingEnabled = true
            puckBearing = PuckBearing.HEADING
            enabled = true
        }
    }

    mapViewportState.transitionToFollowPuckState(
        completionListener = { isFinish ->
            if (isFinish) {
                mapViewportState.setCameraOptions { bearing(null) }
            }
        }
    )
}