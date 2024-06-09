package com.oddlyspaced.zomato.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.oddlyspaced.zomato.notification.MainActivity
import com.oddlyspaced.zomato.notification.R

/**
 * @author : hardik
 * @created : 09/06/24, Sunday
 **/
class OrderTrackService : Service() {
    private fun createNotification(): NotificationCompat.Builder {
        val channelId = "foreground_service_channel"
        val channelName = "Foreground Service Channel"

        // Create the NotificationChannel, only for API 26+
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("The service is running in the foreground")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification().build()
        startForeground(1, notification)
    }
}