package com.trio.stride

import com.trio.stride.data.HeartRateRepository
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.trio.stride.data.ble.ConnectionState
import com.trio.stride.data.ble.HeartRateReceiveManager
import com.trio.stride.ui.utils.ble.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordService : LifecycleService() {

    @Inject
    lateinit var heartRateRepository: HeartRateRepository

    @Inject
    lateinit var heartRateReceiveManager: HeartRateReceiveManager


    private var notificationBuilder: NotificationCompat.Builder? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var observeJob: Job? = null


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
                observeDistance()
                startForeground()
            }

            STOP_RECORDING -> {
                Log.d("bluetoothScan", "stop foreground")
//                observeJob?.cancel()
                stopForeground(STOP_FOREGROUND_REMOVE)
//                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun startForeground() {
        startForeground(NOTIFICATION_ID, buildNotification(0, 0f))
    }

    private fun observeDistance() {
        observeJob?.cancel()
        observeJob = serviceScope.launch {
            heartRateRepository.distance.collect{dist ->
                updateNotification(0, dist)
            }
        }
    }

    private fun observeBleData() {
        serviceScope.launch {
            heartRateReceiveManager.data.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        heartRateRepository.updateHeartRate(result.data.heartRate)
                        heartRateRepository.updateConnectionState(result.data.connectionState)
                    }

                    is Resource.Loading -> {
                        heartRateRepository.updateConnectionState(ConnectionState.CurrentlyInitializing)
                    }

                    is Resource.Error -> {
                        heartRateRepository.updateConnectionState(ConnectionState.Uninitialized)
                    }
                }
            }
        }
    }

    private fun updateNotification(heartRate: Int, distance: Float) {
        val notification = buildNotification(heartRate, distance)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(heartRate: Int, distance: Float): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Workout in progress")
            .setContentText("HR: $heartRate bpm | Distance: ${"%.2f".format(distance)} km")
            .setSmallIcon(R.drawable.activity) // Replace with your icon
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
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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


    }
}


