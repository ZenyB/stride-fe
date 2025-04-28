package com.trio.stride.ui.utils.map

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.runtime.Composable
import com.mapbox.maps.MapView
import com.trio.stride.ui.screens.record.RecordViewModel

object GpsUtils {
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @Composable
    fun createGpsLauncher(
        context: Context,
        mapView: MapView?,
        updateGpsStatus: (RecordViewModel.GPSStatus) -> Unit,
    ) = rememberLauncherForActivityResult(
        contract = StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "GPS on!", Toast.LENGTH_SHORT).show()
            updateGpsStatus(RecordViewModel.GPSStatus.ACQUIRING_GPS)
            focusToUser(mapView)
        } else {
            Toast.makeText(context, "Can't access current location", Toast.LENGTH_SHORT).show()
            updateGpsStatus(RecordViewModel.GPSStatus.NO_GPS)
        }
    }
}