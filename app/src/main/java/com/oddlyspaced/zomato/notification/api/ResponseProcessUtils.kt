package com.oddlyspaced.zomato.notification.api

import com.oddlyspaced.zomato.notification.api.model.OrderDetailsItem
import com.oddlyspaced.zomato.notification.api.model.OrderDetailsResponse
import com.oddlyspaced.zomato.notification.api.model.OrderHistoryItem
import com.oddlyspaced.zomato.notification.api.model.OrderHistorySnippet
import com.oddlyspaced.zomato.notification.api.model.OrderStatus

/**
 * @author : hardik
 * @created : 10/06/24, Monday
 **/

fun parseOrderHistoryResult(result: OrderHistorySnippet): OrderHistoryItem {
    var title = result.topContainer.title.text
    title = title.replace("<medium-400|{grey-900|", "").replace("}>", "")
    var orderId = result.clickAction.deeplink.url
    orderId = orderId.replace("zomato://delivery/", "").replace("zomato://order_summary/", "")
    val orderStatus = result.topContainer.tag?.title?.text ?: "Unknown"
    val orderTime = result.bottomContainer.title.text
    return OrderHistoryItem(title, orderId, orderTime, orderStatus)
}

fun parseOrderResponse(result: OrderDetailsResponse): OrderDetailsItem {
    val restaurantName = result.response.orderDetails.restaurantName
    val orderId = result.response.orderDetails.tagId
    val restaurantId = result.response.orderDetails.resId
    val status = OrderStatus.valueOf(result.response.orderDetails.status)
    val estimatedTime =
        result.response.headerData.pillData?.leftData?.title?.text ?: "Enjoy your order!"
    val estimatedTimeDesc = result.response.headerData.subtitle2?.text ?: ""
    val orderStatusDesc = result.response.headerData.pillData?.rightData?.title?.text ?: "Delivered"
    val mapData = result.response.mapData?.markers ?: arrayListOf()
    var progress = 0F
    try {
        var remainingTimeInNum = estimatedTime.filter {
            it.isDigit()
        }.toInt()
        // second half
        when (status) {
            OrderStatus.DELIVERED -> {
                progress = 264F
            }

            OrderStatus.ON_THE_WAY -> {
                if (remainingTimeInNum > 60) {
                    remainingTimeInNum = 60
                }
                // todo: replace time with distance in future
                // min -> max = 128 to 264, thus multiply remaining time with percent of progress
                // less the time, more the percent
                // percentage is out of 60
                progress = 128F + ((264F - 128F) * (((60 - remainingTimeInNum) / 60) * 100))
            }

            OrderStatus.IN_KITCHEN_RIDER_ARRIVED -> {
                progress = 92F
            }

            OrderStatus.IN_KITCHEN_RIDER_NOT_ASSIGNED -> {
                // todo
                progress = 0F
            }

            else -> {}
        }
    } catch (e: Exception) {
        if (status == OrderStatus.DELIVERED) {
            progress = 264F
        }
    }

    return OrderDetailsItem(
        orderId,
        restaurantId,
        status,
        restaurantName,
        estimatedTime,
        estimatedTimeDesc,
        orderStatusDesc,
        mapData,
        progress
    )
}