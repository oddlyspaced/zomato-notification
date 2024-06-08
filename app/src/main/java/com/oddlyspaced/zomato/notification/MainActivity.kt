package com.oddlyspaced.zomato.notification

import android.Manifest.permission.POST_NOTIFICATIONS
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oddlyspaced.zomato.notification.ui.theme.ZomatoNotificationTheme
import com.oddlyspaced.zomato.notification.vm.MainViewModel

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
        setContent {
            ZomatoNotificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotificationSection(mainVM, requestNotifPerm)
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