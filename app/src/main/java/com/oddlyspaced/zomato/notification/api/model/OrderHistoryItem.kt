package com.oddlyspaced.zomato.notification.api.model

/**
 * @author : hardik
 * @created : 09/06/24, Sunday
 **/

data class OrderHistoryItem(
    val restaurant: String,
    val orderId: String,
    val orderTime: String,
    val status: String,
)