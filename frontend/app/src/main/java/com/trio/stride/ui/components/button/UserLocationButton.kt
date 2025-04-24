package com.trio.stride.ui.components.button

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.map.checkLocationOn

@Composable
fun UserLocationButton(
    mapViewportState: MapViewportState,
    mapView: MapView?,
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
            checkLocationOn(context, mapViewportState, mapView, launcher, action)
        }) {
        Icon(
            modifier = iconModifier,
            painter = painterResource(R.drawable.user_location_icon),
            contentDescription = "Focus to Location",
            tint = StrideTheme.colorScheme.onBackground
        )
    }
}