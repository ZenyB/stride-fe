package com.trio.stride.data.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.geojson.Point
import com.trio.stride.MainActivity
import com.trio.stride.R
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.data.ble.HeartRateReceiveManager
import com.trio.stride.data.ble.HeartRateResult
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.repositoryimpl.RecordRepository
import com.trio.stride.ui.utils.ble.Resource
import com.trio.stride.ui.utils.formatTimeByMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordService : LifecycleService() {

    @Inject
    lateinit var recordRepository: RecordRepository

    @Inject
    lateinit var heartRateReceiveManager: HeartRateReceiveManager

    @Inject
    lateinit var sportManager: SportManager


    private var notificationBuilder: NotificationCompat.Builder? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var observeJob: Job? = null
    private var timeJob: Job? = null

    private var isPaused = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {

            START_RECEIVING -> {
                heartRateReceiveManager.startReceiving()
            }

            CONNECT_TO_DEVICE -> {
                val device = intent
                    .getParcelableExtra<BluetoothDevice>(EXTRA_DEVICE)
                device?.let { heartRateReceiveManager.connectToDevice(it) }
                observeBleData()
            }

            RECONNECT -> {
                heartRateReceiveManager.reconnect()
            }

            DISCONNECT -> {
                heartRateReceiveManager.disconnect()
            }

            CLOSE_CONNECTION -> {
                heartRateReceiveManager.closeConnection()
            }

            START_RECORDING -> {
                Log.d("bluetoothScan", "start foreground")
                observeDistanceAndTime()
                startTimer()
                startForeground()
                if (sportManager.currentSport.value?.sportMapType != null) {
                    startTracking()
                }
            }

            PAUSE_RECORDING -> {
                isPaused = true
            }

            RESUME_RECORDING -> {
                isPaused = false
            }

            STOP_RECORDING -> {
                Log.d("bluetoothScan", "stop foreground")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopTimer()
                stopTracking()
            }
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        createNotificationChannel()

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filter)
    }

    private fun startForeground() {
        startForeground(NOTIFICATION_ID, buildNotification(0, 0.0))
    }

    private fun observeDistanceAndTime() {
        observeJob?.cancel()
        observeJob = serviceScope.launch {
            combine(
                recordRepository.distance,
                recordRepository.time
            ) { dist, time ->
                Pair(dist, time)
            }.collect { (dist, time) ->
                updateNotification(time, dist)
            }
        }
    }

    private fun observeBleData() {
        serviceScope.launch {
            heartRateReceiveManager.data.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        recordRepository.updateHeartRate(result.data.heartRate)
                        recordRepository.updateConnectionState(result.data.connectionState)
                    }

                    is Resource.Loading -> {
                        recordRepository.updateConnectionState(ConnectionState.CurrentlyInitializing)
                    }

                    is Resource.Error -> {
                        recordRepository.updateConnectionState(ConnectionState.Uninitialized)
                    }
                }
            }
        }
    }

    private fun startTimer() {
        timeJob?.cancel()
        timeJob = serviceScope.launch {
            isPaused = false
            val startTime = System.currentTimeMillis()
            var lastUpdateTime = startTime
            var movingTime = 0L

            while (true) {
                delay(1000)

                if (isPaused) {
                    lastUpdateTime = System.currentTimeMillis()
                    continue
                }

                val currentTime = System.currentTimeMillis()
                val elapsedSinceLastUpdate =
                    currentTime - lastUpdateTime

                movingTime += elapsedSinceLastUpdate

                val elapsedTime = currentTime - startTime
                recordRepository.updateElapsedTime(elapsedTime)

                recordRepository.updateTime(movingTime)

                if (sportManager.currentSport.value?.sportMapType != null) {
                    val durationSeconds = movingTime / 1000.0
                    val distance = recordRepository.distance.value
                    val avgSpeed =
                        if (durationSeconds > 0.0) (distance / durationSeconds) * 3.6 else 0.0
                    recordRepository.updateAvgSpeed(avgSpeed)
                }

                lastUpdateTime = currentTime
            }
        }
    }

    private fun stopTimer() {
        timeJob?.cancel()
        timeJob = null
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTracking() {
        if (!hasLocationPermission()) return

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let { location ->
                    val point = Point.fromLngLat(location.longitude, location.latitude)
                    recordRepository.addPoints(point)
                }
            }
        }

        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(
                applicationContext,
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
                e.printStackTrace()
                Log.e("Location", "Permission error: ${e.message}")
            }
        } else {
            Log.w("Location", "Permission not granted")
        }
    }

    private fun stopTracking() {
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun updateNotification(time: Long, distance: Double) {
        val notification = buildNotification(time, distance)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(time: Long, distance: Double): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Workout in progress")
            .setContentText(
                "${formatTimeByMillis(time)} | Distance: ${
                    "%.2f".format(
                        distance / 1000
                    )
                } km"
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        return notificationBuilder!!.build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Workout Tracking",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "workout_channel"
        const val NOTIFICATION_ID = 1
        const val CONNECT_TO_DEVICE = "CONNECT_TO_DEVICE"
        const val RECONNECT = "RECONNECT"
        const val DISCONNECT = "DISCONNECT"
        const val START_RECEIVING = "START_RECEIVING"
        const val CLOSE_CONNECTION = "CLOSE_CONNECTION"
        const val START_RECORDING = "START_RECORDING"
        const val STOP_RECORDING = "STOP_RECORDING"
        const val PAUSE_RECORDING = "PAUSE_RECORDING"
        const val RESUME_RECORDING = "RESUME_RECORDING"
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_ON -> {
                        heartRateReceiveManager.setBluetoothState(true)
                        heartRateReceiveManager.reconnect()
                    }

                    BluetoothAdapter.STATE_OFF -> {
                        heartRateReceiveManager.setBluetoothState(false)
                        heartRateReceiveManager.disconnect()
                        heartRateReceiveManager.closeConnection()

                        serviceScope.launch {
                            heartRateReceiveManager.data.emit(
                                Resource.Success(
                                    data = HeartRateResult(
                                        0,
                                        ConnectionState.Disconnected
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


