package com.trio.stride.ui.utils.map

import android.content.Context
import android.location.LocationManager

object GpsUtils {
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}