package com.trio.stride.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.trio.stride.MainActivity
import com.trio.stride.R
import com.trio.stride.domain.usecase.fcmnotification.RefreshAndSaveFCMTokenUseCase
import com.trio.stride.navigation.Screen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

@AndroidEntryPoint
class FCMNotificationService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("ON_NEW_FCM_TOKEN", token)

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            FcmServiceEntryPoint::class.java
        )
        val refreshFCMToken = entryPoint.refreshAndSaveFCMTokenUseCase()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                refreshFCMToken.invoke()
            } catch (e: Exception) {
                Log.e("FCM", "Failed to save token: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.data.isNotEmpty()) {
            val title = message.data["title"] ?: "Stride"
            val body = message.data["message"] ?: ""
            val banner = message.data["banner"]
            val bitmap = banner?.let { getBitmapFromUrl(it) }
            val notificationId = System.currentTimeMillis().toInt()
            Log.i("NOTIFICATION_SHOW", "Data: $title - $body - $banner")
            showNotification(title, body, bitmap)
        } else {
            message.notification?.let {
                val title = it.title ?: "Stride"
                val body = it.body ?: ""
                Log.i("NOTIFICATION_SHOW", "Notification: $title - $body")
                showNotification(title, body)
            }
        }
    }


    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val input = URL(imageUrl).openStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Stride Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        Log.i("NOTIFICATION_CHANNEL_CREATE", "created")
    }

    private fun showNotification(
        title: String,
        message: String,
        banner: Bitmap? = null,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val requestCode = 1002
        val routeIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("navigateTo", Screen.NotificationScreen.route)
        }

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            requestCode,
            routeIntent,
            flags
        )

        Log.i("SHOW_NOTIFICATION_INTENT_ROUTE", routeIntent.extras?.toString() ?: "No extras")

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_primary)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(banner))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.i("NOTIFICATION_BUILD", "build")
    }

    companion object {
        const val CHANNEL_ID = "stride_notification"
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface FcmServiceEntryPoint {
    fun refreshAndSaveFCMTokenUseCase(): RefreshAndSaveFCMTokenUseCase
}
