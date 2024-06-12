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
import com.oddlyspaced.zomato.notification.api.model.OrderStatus
import com.oddlyspaced.zomato.notification.api.parseOrderResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
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
                    setAndShowRiderProgress(orderId)
                }
            }
        }
    }

    private val notificationLayoutSmall by lazy {
        RemoteViews(
            packageName,
            R.layout.zomato_notification_small
        )
    }
    private val notificationLayoutExpanded by lazy {
        RemoteViews(packageName, R.layout.zomato_notification_expanded)
    }

    private fun setAndShowRiderProgress(orderId: Long) {
        // 264 MAX
        // 0 MIN
        // 92 ARRIVED WHOLE
        // 128 ON WAY START
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val orderResp = api.getOrderDetails(orderId)
                val parsedDetails = parseOrderResponse(orderResp)
                notificationLayoutSmall.apply {
                    setTextViewText(
                        R.id.zom_notification_title,
                        "${parsedDetails.restaurantName} • ${parsedDetails.statusDesc}"
                    )
                }
                notificationLayoutExpanded.apply {
                    setTextViewText(R.id.zom_restaurant, parsedDetails.restaurantName)
                    setTextViewText(R.id.zom_status_title, parsedDetails.statusDesc)
                    setTextViewText(R.id.zom_status_type, parsedDetails.estimateTimeDesc)
                    setTextViewText(R.id.zom_status_time, parsedDetails.estimatedTime)
                    setViewLayoutMargin(
                        R.id.zom_rider,
                        RemoteViews.MARGIN_START,
                        parsedDetails.progressPadding,
                        TypedValue.COMPLEX_UNIT_DIP
                    )
                }
                // re use notification builder
                createNotification().let {
                    it.setCustomBigContentView(notificationLayoutExpanded)
                    notificationManager.notify(
                        (orderId / 1000).toInt(),
                        it.build()
                    ) // todo: improve order id long -> int logic
                }
                if (parsedDetails.status == OrderStatus.DELIVERED) {
                    // done
                } else {
                    delay(100 * 30) // 30 secs
                    setAndShowRiderProgress(orderId)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error occurred while fetching details = $orderId")
                Log.d(TAG, e.stackTraceToString())
            }
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
            .setCustomContentView(notificationLayoutSmall)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setContentIntent(pendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(receiver, IntentFilter(ACTION), RECEIVER_EXPORTED)
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        val notification =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID).setContentTitle("Running").setSmallIcon(R.drawable.ic_checkpoint_restaurant).build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        }
        catch (_: Exception){}
    }
}