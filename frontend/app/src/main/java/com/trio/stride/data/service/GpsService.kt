package com.trio.stride.data.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.trio.stride.data.repositoryimpl.GpsRepository
import com.trio.stride.ui.screens.record.RecordViewModel
import com.trio.stride.ui.utils.map.GpsUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GpsService : LifecycleService() {

    @Inject
    lateinit var gpsRepository: GpsRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var gpsMonitorJob: Job? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startGpsMonitoring()

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val gpsFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(gpsStateReceiver, gpsFilter)
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission())
            return

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(1000)
            .build()

        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                gpsRepository.updateGpsStatus(RecordViewModel.GPSStatus.NO_GPS)
                e.printStackTrace()
                Log.e("Location", "Permission error: ${e.message}")
            }
        } else {
            gpsRepository.updateGpsStatus(RecordViewModel.GPSStatus.NO_GPS)
            Log.w("Location", "Permission not granted")
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkGpsStatus(): Boolean {
        val currentStatus = when {
            !hasLocationPermission() -> {
                RecordViewModel.GPSStatus.NO_GPS to "Location permission not granted"
            }

            !GpsUtils.isGpsEnabled(this) -> {
                RecordViewModel.GPSStatus.NO_GPS to "GPS is disabled in device settings"
            }

            else -> {
                RecordViewModel.GPSStatus.ACQUIRING_GPS to "Acquiring GPS signal"
            }
        }

        // Only update if status changed
        if (currentStatus.first != gpsRepository.gpsStatus.value) {
            gpsRepository.updateGpsStatus(currentStatus.first)
            Log.w("RecordService", currentStatus.second)

            if (currentStatus.first == RecordViewModel.GPSStatus.ACQUIRING_GPS) {
                startLocationUpdates()
            }
        }

        return currentStatus.first != RecordViewModel.GPSStatus.NO_GPS
    }

    private fun startGpsMonitoring() {
        gpsMonitorJob?.cancel()
        gpsMonitorJob = serviceScope.launch {
            while (true) {
                delay(30000)
                checkGpsStatus()
            }
        }
    }

    private val gpsStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                checkGpsStatus()
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return

            if (location.accuracy <= 20f) {
                gpsRepository.updateGpsStatus(RecordViewModel.GPSStatus.GPS_READY)
                Log.d("GpsService", "GPS ready with accuracy: ${location.accuracy}")
            }
        }
    }
}