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
import com.oddlyspaced.zomato.notification.api.Api
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

/**
 * @author : hardik
 * @created : 09/06/24, Sunday
 **/
@AndroidEntryPoint
class OrderTrackService : Service() {

    @Inject
    lateinit var api: Api

    companion object {
        private const val TAG = "OrderTrackService"
        const val ACTION = "OrderTrackAction"
        const val CHANNEL_ID = "foreground_service_channel"
        const val CHANNEL_NAME = "Foreground Service Channel"
        const val KEY_ORDER_ID = "order_id"
    }

    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    private fun repeatCor(orderId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repeat(100) {
                val tt = api.getOrderHistory()
                Log.d(TAG, "Status = ${tt.status}")
                setAndShowRiderProgress(orderId, (orderIdProgressMap[orderId] ?: 0F) + 1)
                delay(100 * 10)
            }
        }
    }

    // activity to service communication
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val orderId = intent.extras?.getLong(KEY_ORDER_ID)
                if (orderId != null) {
                    Log.d(
                        TAG,
                        "Received intent = ${intent.action} $orderId"
                    )
                    repeatCor(orderId)
//                    setAndShowRiderProgress(orderId, (orderIdProgressMap[orderId] ?: 0F) + 1)
                }
            }
        }
    }

    private val orderIdProgressMap = hashMapOf<Long, Float>()
    private val orderIdNotificationMap = hashMapOf<Long, NotificationCompat.Builder>()

    private val notificationLayout by lazy {
        RemoteViews(
            packageName,
            R.layout.zomato_notification_small
        )
    }
    private val notificationLayoutExpanded by lazy {
        RemoteViews(packageName, R.layout.zomato_notification_expanded)
    }

    private fun setAndShowRiderProgress(orderId: Long, progress: Float) {
        // 264 MAX
        // 0 MIN
        // 92 ARRIVED WHOLE
        // 128 ON WAY START

        notificationLayoutExpanded.setViewLayoutMargin(
            R.id.zom_rider,
            RemoteViews.MARGIN_START,
            (progress / 100) * 264,
            TypedValue.COMPLEX_UNIT_DIP
        )
        orderIdProgressMap[orderId] = progress
        orderIdNotificationMap[orderId] = createNotification()
        orderIdNotificationMap[orderId]?.let {
            it.setCustomBigContentView(notificationLayoutExpanded)
            notificationManager.notify(
                (orderId / 1000).toInt(),
                it.build()
            ) // todo: improve order id long -> int logic
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        // Create the NotificationChannel, only for API 26+
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
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
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Running").build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}