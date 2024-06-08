package com.oddlyspaced.zomato.notification.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * @author : hardik
 * @created : 08/06/24, Saturday
 **/
class MainViewModel : ViewModel() {
    var isNotificationPermissionGranted by mutableStateOf(false)
}