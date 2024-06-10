package com.oddlyspaced.zomato.notification.api

import com.oddlyspaced.zomato.notification.api.model.MapData
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
    val estimatedTime = result.response.headerData.pillData?.leftData?.title?.text ?: "Enjoy your order!"
    val estimatedTimeDesc = result.response.headerData.subtitle2?.text ?: ""
    val orderStatusDesc = result.response.headerData.pillData?.rightData?.title?.text ?: "Delivered"
    val mapData = result.response.mapData?.markers ?: arrayListOf()
    return OrderDetailsItem(
        orderId,
        restaurantId,
        status,
        restaurantName,
        estimatedTime,
        estimatedTimeDesc,
        orderStatusDesc,
        mapData,
    )
}