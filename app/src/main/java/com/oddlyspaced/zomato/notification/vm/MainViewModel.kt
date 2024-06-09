package com.oddlyspaced.zomato.notification.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddlyspaced.zomato.notification.api.Api
import com.oddlyspaced.zomato.notification.api.model.OrderHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/**
 * @author : hardik
 * @created : 08/06/24, Saturday
 **/

@HiltViewModel
class MainViewModel @Inject constructor(private val api: Api) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    var isNotificationPermissionGranted by mutableStateOf(false)

    private val _orderHistory: MutableStateFlow<OrderHistory> =
        MutableStateFlow(OrderHistory("Not Fetched!"))
    val orderHistory: StateFlow<OrderHistory> = _orderHistory

    fun fetchOrderHistory() {
        viewModelScope.launch {
            try {
                val fetchedOrderHistory = api.getOrderHistory()
                _orderHistory.value = fetchedOrderHistory
            } catch (e: Exception) {
                Log.d(TAG, "Error in fetching order history!")
                Log.d(TAG, e.stackTraceToString())
            }
        }
    }
}