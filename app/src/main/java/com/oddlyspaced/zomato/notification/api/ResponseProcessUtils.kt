package com.oddlyspaced.zomato.notification.api

import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
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

fun parseOrderResponse(result: OrderDetailsResponse): OrderDetailsItem {
    val restaurantName = result.response.orderDetails.restaurantName
    val orderId = result.response.orderDetails.tagId
    val restaurantId = result.response.orderDetails.resId
    val status = OrderStatus.valueOf(result.response.orderDetails.status)
    val estimatedTime =
        result.response.headerData.pillData?.leftData?.title?.text ?: "Enjoy your order!"
    val orderStatusDesc = result.response.headerData.subtitle2?.text ?: ""
    val estimatedTimeDesc =
        result.response.headerData.pillData?.rightData?.title?.text ?: (if (status == OrderStatus.ON_THE_WAY) "Almost there" else "Delivered")
    val mapData = result.response.mapData?.markers ?: arrayListOf()

    val mapDataRestaurant = mapData.filter {
        it.type == "source"
    }[0]
    val mapDataDestination = mapData.filter {
        it.type == "destination"
    }[0]

    val distRestDest = abs(
        distanceInMeter(
            mapDataRestaurant.latitude,
            mapDataRestaurant.longitude,
            mapDataDestination.latitude,
            mapDataDestination.longitude
        )
    )

    var progress = 0F

    when (status) {
        OrderStatus.CONFIRMED, OrderStatus.IN_KITCHEN_RIDER_NOT_ASSIGNED -> {
            progress = OrderTrackService.PROGRESS_REST_START
        }

        OrderStatus.IN_KITCHEN_RIDER_ASSIGNED -> {
            val mapDataRider = mapData.filter {
                it.type == "rider"
            }[0]

            val distRiderRest = abs(
                distanceInMeter(
                    mapDataRider.latitude,
                    mapDataRider.longitude,
                    mapDataRestaurant.latitude,
                    mapDataRestaurant.longitude
                )
            )

            // we assume max distance to be 3000m
            progress =
                abs(((if (3000F - distRiderRest < 0) 0F else (3000F - distRiderRest)) / 3000) * (OrderTrackService.PROGRESS_REST_END - OrderTrackService.PROGRESS_REST_START))

            Log.d(TAG, "Distance Assigned : $distRiderRest $progress")
        }

        OrderStatus.IN_KITCHEN_RIDER_ARRIVED -> {
            progress = OrderTrackService.PROGRESS_REST_END

        }

        OrderStatus.ON_THE_WAY -> {
            val mapDataRider = mapData.filter {
                it.type == "rider"
            }[0]
            val distRiderDest = abs(
                distanceInMeter(
                    mapDataRider.latitude,

                    mapDataRider.longitude,
                    mapDataDestination.latitude,
                    mapDataDestination.longitude
                )
            )

            progress = OrderTrackService.PROGRESS_DEST_START + (abs(1 - (distRiderDest / distRestDest))) * (OrderTrackService.PROGRESS_DEST_END - OrderTrackService.PROGRESS_DEST_START)
            Log.d(TAG, "ON WAY $distRestDest $distRiderDest $progress")
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
        mapData,
        progress
    )
}