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
import com.trio.stride.navigation.Screen
import com.trio.stride.receiver.RecordReceiver
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

        Log.i("RecordService", "Received intent: ${intent?.action}")

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
                startRecording()
            }

            PAUSE_RECORDING -> {
                pauseRecording()
            }

            RESUME_RECORDING -> {
                resumeRecording()
            }

            STOP_RECORDING -> {
                endRecording()
            }

            SAVING_RECORDING -> {
                savingRecording()
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
        startForeground(
            NOTIFICATION_ID,
            buildNotification(recordRepository.sportName.value, 0, 0.0, true)
        )
    }

    private fun observeRecordValues() {
        observeJob?.cancel()
        observeJob = serviceScope.launch {
            combine(
                recordRepository.sportName,
                recordRepository.distance,
                recordRepository.time,
                recordRepository.recording
            ) { sportName, dist, time, recording ->
                Quadruple(sportName, dist, time, recording)
            }.collect { (sportName, dist, time, recording) ->
                updateNotification(sportName, time, dist, recording)
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

    private fun startRecording() {
        Log.d("bluetoothScan", "start foreground")
        observeRecordValues()
        startTimer()
        startForeground()
        if (sportManager.currentSport.value?.sportMapType != null) {
            startTracking()
        }
    }

    private fun pauseRecording() {
        Log.i("PAUSE_RECORDINGG", "saving")
        isPaused = true
        recordRepository.stop()
        updateNotification(
            recordRepository.sportName.value,
            recordRepository.time.value,
            recordRepository.distance.value,
            recordRepository.recording.value
        )
    }

    private fun resumeRecording() {
        Log.i("RESUME_RECORDINGG", "saving")
        isPaused = false
        recordRepository.resume()
        updateNotification(
            recordRepository.sportName.value,
            recordRepository.time.value,
            recordRepository.distance.value,
            recordRepository.recording.value
        )
    }

    private fun savingRecording() {
        Log.i("SAVING_RECORDINGG", "saving")
        isPaused = true
        recordRepository.finish()
    }

    private fun endRecording() {
        Log.i("END_RECORDINGG", "end")
        Log.d("bluetoothScan", "stop foreground")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        stopTimer()
        stopTracking()
        recordRepository.end()
    }

    private fun updateNotification(
        sportName: String,
        time: Long,
        distance: Double,
        recording: Boolean
    ) {
        val notification = buildNotification(sportName, time, distance, recording)
        startForeground(NOTIFICATION_ID, notification)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(
        sportName: String,
        time: Long,
        distance: Double,
        recording: Boolean
    ): Notification {
        var requestCode = 1001

        val routeIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("navigateTo", Screen.BottomNavScreen.Record.route)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            requestCode,
            routeIntent,
            flags
        )

        val actionIntentAction =
            if (recording) RecordReceiver.ACTION_TYPE_STOP else RecordReceiver.ACTION_TYPE_START
        val actionIntent = Intent(applicationContext, RecordReceiver::class.java).apply {
            action = actionIntentAction
        }

        requestCode = 1
        val actionPendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val actionText = if (recording) "Stop" else "Resume"
        val contentText = if (!recording) "Stopped" else null

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(
                "$sportName | ${formatTimeByMillis(time)} | ${
                    "%.2f".format(
                        distance / 1000
                    )
                } km"
            )
            .setContentText(
                contentText
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        contentText
                    )
            )
            .setSmallIcon(R.drawable.ic_flag)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_flag, actionText, actionPendingIntent)


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
        const val SAVING_RECORDING = "SAVING_RECORDING"
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

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)


