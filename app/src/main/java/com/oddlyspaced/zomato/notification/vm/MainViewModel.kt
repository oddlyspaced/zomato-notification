package com.oddlyspaced.zomato.notification.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddlyspaced.zomato.notification.api.Api
import com.oddlyspaced.zomato.notification.api.model.OrderHistoryItem
import com.oddlyspaced.zomato.notification.api.model.OrderHistorySnippet
import com.oddlyspaced.zomato.notification.api.parseOrderHistoryResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : hardik
 * @created : 08/06/24, Saturday
 **/

@HiltViewModel
class MainViewModel @Inject constructor(private val api: Api) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        const val STATUS_NOT_FETCHED = 1
        const val STATUS_FETCH_ERROR = 2
        const val STATUS_FETCH_SUCCESS = 3
        const val STATUS_FETCHING = 4
    }

    var isNotificationPermissionGranted by mutableStateOf(false)

    private val _orderHistory: MutableStateFlow<List<OrderHistoryItem>> = MutableStateFlow(listOf())
    val orderHistory: StateFlow<List<OrderHistoryItem>> = _orderHistory

    private val _orderHistoryFetchStatus: MutableStateFlow<Int> = MutableStateFlow(
        STATUS_NOT_FETCHED
    )
    val orderHistoryFetchStatus: StateFlow<Int> = _orderHistoryFetchStatus

    fun fetchOrderHistory() {
        _orderHistoryFetchStatus.value = STATUS_FETCHING
        viewModelScope.launch {
            try {
                val fetchedOrderHistory = api.getOrderHistory()
                val items = arrayListOf<OrderHistoryItem>()
                fetchedOrderHistory.results.forEach { result ->
                    result.orderHistorySnippet?.let { snippet ->
                        items.add(
                            parseOrderHistoryResult(snippet)
                        )
                    }
                }
                _orderHistory.value = items
                _orderHistoryFetchStatus.value = STATUS_FETCH_SUCCESS
            } catch (e: Exception) {
                Log.d(TAG, "Error in fetching order history!")
                Log.d(TAG, e.stackTraceToString())
                _orderHistoryFetchStatus.value = STATUS_FETCH_ERROR
            }
        }
    }
}