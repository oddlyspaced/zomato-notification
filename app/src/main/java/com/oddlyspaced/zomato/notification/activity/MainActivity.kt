package com.oddlyspaced.zomato.notification.activity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oddlyspaced.zomato.notification.service.OrderTrackService
import com.oddlyspaced.zomato.notification.ui.theme.ZomatoNotificationTheme
import com.oddlyspaced.zomato.notification.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainVM.isNotificationPermissionGranted =
            checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        val requestNotifPerm =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                mainVM.isNotificationPermissionGranted = isGranted
            }
        val serviceIntent = Intent(this, OrderTrackService::class.java)
        startForegroundService(serviceIntent)

        setContent {
            ZomatoNotificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        NotificationSection(mainVM, requestNotifPerm)
                        CommunicationSection(context = applicationContext)
                        PostList(mainVM, context = applicationContext)
                    }
                }
            }
        }
    }
}


@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(Locale.ROOT),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp,
        modifier = modifier
    )
}

@Composable
fun NotificationSection(
    vm: MainViewModel = viewModel(),
    requestPermission: ActivityResultLauncher<String>
) {
    Column {
        SectionTitle(text = "Notification")
        Text(
            text = "Notification Permission ${if (vm.isNotificationPermissionGranted) "Granted" else "Not Granted"}",
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(onClick = {
            requestPermission.launch(POST_NOTIFICATIONS)
        }, enabled = !vm.isNotificationPermissionGranted, modifier = Modifier.padding(top = 4.dp)) {
            Text("Grant Notification Permission")
        }
    }
}

@Composable
fun CommunicationSection(
    context: Context
) {
    Column {
        SectionTitle(text = "Service Controls")
        Button(onClick = {
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Test Service Communication")
        }
    }
}

@Composable
fun PostList(
    vm: MainViewModel = viewModel(),
    context: Context,
) {
    val orderHistory by vm.orderHistory.collectAsState()
    val orderHistoryFetchStatus by vm.orderHistoryFetchStatus.collectAsState()

    var launchEffectKey by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = launchEffectKey) {
        if (launchEffectKey) {
            vm.fetchOrderHistory()
        }
    }

    Column {
        SectionTitle(text = "Order History")
        Text(
            when (orderHistoryFetchStatus) {
                MainViewModel.STATUS_NOT_FETCHED -> "Order History Not Fetched"
                MainViewModel.STATUS_FETCH_ERROR -> "Error in fetching Order History!"
                MainViewModel.STATUS_FETCH_SUCCESS -> "Order History fetched Successfully"
                MainViewModel.STATUS_FETCHING -> "Fetching Order History..."
                else -> ""
            },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(onClick = {
            launchEffectKey = true
        }, modifier = Modifier.padding(top = 4.dp)) {
            Text("Fetch Orders")
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            orderHistory.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(it.restaurant)
                        Text(it.orderId)
                        Text(it.orderTime)
                    }
                    Button(onClick = {
                        context.sendBroadcast(Intent(OrderTrackService.ACTION).apply {
                            putExtra(OrderTrackService.KEY_ORDER_ID, it.orderId.toLong())
                        })
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text(it.status)
                    }
                }
            }
        }
    }
}