package com.trio.stride.ui.utils.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.mapbox.geojson.Point

object LocationUtils {
    fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient,
        context: Context,
        onLocationReceived: (Point) -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(Point.fromLngLat(location.longitude, location.latitude))
            }
        }
    }
}