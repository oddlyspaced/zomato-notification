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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oddlyspaced.zomato.notification.service.OrderTrackService
import com.oddlyspaced.zomato.notification.ui.theme.ZomatoNotificationTheme
import com.oddlyspaced.zomato.notification.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                    Column {
                        NotificationSection(mainVM, requestNotifPerm)
                        CommunicationSection(applicationContext)
                        PostList(mainVM)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSection(
    vm: MainViewModel = viewModel(),
    requestPermission: ActivityResultLauncher<String>
) {
    Column {
        Text(
            text = "Notification Permission ${if (vm.isNotificationPermissionGranted) "Granted" else "Not Granted"}",
        )
        if (!vm.isNotificationPermissionGranted) {
            Button(onClick = {
                requestPermission.launch(POST_NOTIFICATIONS)
            }) {
                Text("Grant Notification Permission")
            }
        }
    }
}

@Composable
fun CommunicationSection(
    context: Context
) {
    Column {
        Button(onClick = {
            context.sendBroadcast(Intent(OrderTrackService.ACTION))
        }) {
            Text("Test Service Communication")
        }
    }
}

@Composable
fun PostList(
    vm: MainViewModel = viewModel(),
) {
    val orderHistory by vm.orderHistory.collectAsState()
    var launchEffectKey by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = launchEffectKey) {
        if (launchEffectKey) {
            vm.fetchOrderHistory()
        }
    }

    Button(onClick = {
        launchEffectKey = true
    }) {
        Text("Fetch")
    }

    Text("${orderHistory.status}!")
}