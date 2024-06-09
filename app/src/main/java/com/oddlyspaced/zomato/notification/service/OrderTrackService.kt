package com.oddlyspaced.zomato.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.oddlyspaced.zomato.notification.activity.MainActivity
import com.oddlyspaced.zomato.notification.R

/**
 * @author : hardik
 * @created : 09/06/24, Sunday
 **/
class OrderTrackService : Service() {

    companion object {
        private const val TAG = "OrderTrackService"
        const val ACTION = "OrderTrackAction"
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Received intent = ${intent?.action}")
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        // Create the NotificationChannel, only for API 26+
        val channelId = "foreground_service_channel"
        val channelName = "Foreground Service Channel"
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        // Get the layouts to use in the custom notification.
        val notificationLayout = RemoteViews(packageName, R.layout.zomato_notification_small)
        val notificationLayoutExpanded =
            RemoteViews(packageName, R.layout.zomato_notification_expanded)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        // Apply the layouts to the notification.
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setContentIntent(pendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(receiver, IntentFilter(ACTION), RECEIVER_EXPORTED)
        val notification = createNotification().build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}