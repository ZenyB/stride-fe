package com.trio.stride

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Intent
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
import com.trio.stride.data.RecordRepository
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.data.ble.HeartRateReceiveManager
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
                startTracking()
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
        timeJob?.cancel()  // Hủy job cũ nếu có
        timeJob = serviceScope.launch {
            val startTime = System.currentTimeMillis()  // Thời gian bắt đầu
            var lastUpdateTime =
                startTime  // Thời gian cập nhật cuối cùng, ban đầu là thời điểm start
            var timePaused = 0L  // Tổng thời gian đã pause

            while (true) {
                delay(1000)  // Đợi 1 giây

                if (isPaused) {
                    // Nếu paused, lưu lại thời gian pause
                    lastUpdateTime = System.currentTimeMillis()
                    continue  // Tiếp tục vòng lặp mà không tính thời gian
                }

                // Nếu không paused, tính toán thời gian đã trôi qua
                val currentTime = System.currentTimeMillis()
                val elapsedSinceLastUpdate =
                    currentTime - lastUpdateTime  // Thời gian trôi qua kể từ lần update trước

                timePaused += elapsedSinceLastUpdate  // Cập nhật thời gian đã trôi qua

                recordRepository.updateTime(timePaused)

                // Tính toán tốc độ trung bình
                val durationSeconds = timePaused / 1000.0 //seconds
                val distance = recordRepository.distance.value //meters
                val avgSpeed =
                    if (durationSeconds > 0.0) (distance / durationSeconds) * 3.6 else 0.0
                recordRepository.updateAvgSpeed(avgSpeed)

                // Cập nhật thời gian đã trôi qua sau mỗi vòng lặp
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

    fun startTracking() {
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

    fun stopTracking() {
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
}


