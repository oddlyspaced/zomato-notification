package com.oddlyspaced.zomato.notification.api

import com.oddlyspaced.zomato.notification.api.model.OrderHistoryItem
import com.oddlyspaced.zomato.notification.api.model.OrderHistorySnippet

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