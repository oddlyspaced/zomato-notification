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
import android.util.TypedValue
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

    private var progress = 0F
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setRiderProgress(progress)
            Log.d(TAG, "Received intent = ${intent?.action} ${progress++}")
        }
    }

    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    private val notificationLayout by lazy {
        RemoteViews(
            packageName,
            R.layout.zomato_notification_small
        )
    }
    private val notificationLayoutExpanded by lazy {
        RemoteViews(packageName, R.layout.zomato_notification_expanded)
    }

    private val orderNotification by lazy { createNotification() }

    private fun setRiderProgress(progress: Float) {
        // 264 MAX
        // 0 MIN
        // 92 ARRIVED WHOLE
        // 128 ON WAY START
        notificationLayoutExpanded.setViewLayoutMargin(
            R.id.zom_rider,
            RemoteViews.MARGIN_START,
            progress,
            TypedValue.COMPLEX_UNIT_DIP
        )
        orderNotification.setCustomBigContentView(notificationLayoutExpanded)
        notificationManager.notify(1, orderNotification.build())
    }

    private fun createNotification(): NotificationCompat.Builder {
        // Create the NotificationChannel, only for API 26+
        val channelId = "foreground_service_channel"
        val channelName = "Foreground Service Channel"
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
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
        val notification = orderNotification.build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}