package com.oddlyspaced.zomato.notification.api

import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import com.oddlyspaced.zomato.notification.api.model.MapMarker
import com.oddlyspaced.zomato.notification.api.model.OrderDetailsItem
import com.oddlyspaced.zomato.notification.api.model.OrderDetailsResponse
import com.oddlyspaced.zomato.notification.api.model.OrderHistoryItem
import com.oddlyspaced.zomato.notification.api.model.OrderHistorySnippet
import com.oddlyspaced.zomato.notification.api.model.OrderStatus
import com.oddlyspaced.zomato.notification.service.OrderTrackService
import kotlin.math.abs

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

private fun distanceInMeter(
    startLat: Float,
    startLon: Float,
    endLat: Float,
    endLon: Float
): Float {
    val results = FloatArray(1)
    Location.distanceBetween(
        startLat.toDouble(),
        startLon.toDouble(),
        endLat.toDouble(),
        endLon.toDouble(),
        results
    )
    return results[0]
}

fun distanceMapMarkers(mm1: MapMarker, mm2: MapMarker) =
    abs(distanceInMeter(mm1.latitude, mm1.longitude, mm2.latitude, mm2.longitude))

// mapDataRiderStart = first known location of the rider
fun parseOrderResponse(
    result: OrderDetailsResponse,
    mapDataRiderStart: MapMarker? = null
): OrderDetailsItem {
    val restaurantName = result.response.orderDetails.restaurantName
    val orderId = result.response.orderDetails.tagId
    val restaurantId = result.response.orderDetails.resId
    val status = OrderStatus.valueOf(result.response.orderDetails.status)
    val estimatedTime =
        result.response.headerData.pillData?.leftData?.title?.text ?: "Enjoy your order!"
    val orderStatusDesc = result.response.headerData.subtitle2?.text ?: ""
    val estimatedTimeDesc =
        result.response.headerData.pillData?.rightData?.title?.text
            ?: (if (status == OrderStatus.ON_THE_WAY) "Almost there" else "Delivered")
    val mapData = result.response.mapData?.markers ?: arrayListOf()

    // this would not be available if order delivered!!!
    val mapDataRestaurant = mapData.filter {
        it.type == "source"
    }.let {
        if (it.isEmpty()) null else it[0]
    }

    val mapDataDestination = mapData.filter {
        it.type == "destination"
    }.let {
        if (it.isEmpty()) null else it[0]
    }

    val mapDataRider = mapData.filter {
        it.type == "rider"
    }.let {
        if (it.isEmpty()) null else it[0]
    }

    var progress = 0F

    when (status) {
        OrderStatus.CONFIRMED, OrderStatus.IN_KITCHEN_RIDER_NOT_ASSIGNED -> {
            progress = OrderTrackService.PROGRESS_REST_START
        }

        OrderStatus.IN_KITCHEN_RIDER_ASSIGNED -> {
            if (mapDataRestaurant != null && mapDataRider != null && mapDataRiderStart != null) {
                val distRiderRestMax = distanceMapMarkers(mapDataRiderStart, mapDataRestaurant)
                val distRiderRestCurrent = distanceMapMarkers(mapDataRider, mapDataRestaurant)
                progress =
                    OrderTrackService.PROGRESS_REST_START + abs(1 - (distRiderRestCurrent / distRiderRestMax)) * (OrderTrackService.PROGRESS_REST_END - OrderTrackService.PROGRESS_REST_START)
            }
        }

        OrderStatus.IN_KITCHEN_RIDER_ARRIVED -> {
            progress = OrderTrackService.PROGRESS_REST_END
        }

        OrderStatus.ON_THE_WAY -> {
            if (mapDataRestaurant != null && mapDataRider != null && mapDataDestination != null) {
                val distRiderDest = distanceMapMarkers(mapDataRider, mapDataDestination)
                val distRestDest = distanceMapMarkers(mapDataRestaurant, mapDataDestination)
                progress =
                    OrderTrackService.PROGRESS_DEST_START + abs(1 - (distRiderDest / distRestDest)) * (OrderTrackService.PROGRESS_DEST_END - OrderTrackService.PROGRESS_DEST_START)
            }
        }

        OrderStatus.DELIVERED -> {
            progress = OrderTrackService.PROGRESS_DEST_END
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
        mapDataRestaurant,
        mapDataDestination,
        mapDataRider,
        progress
    )
}