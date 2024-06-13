package com.oddlyspaced.zomato.notification.api.model

/**
 * @author : hardik
 * @created : 10/06/24, Monday
 **/

enum class OrderStatus(val orderStatus: String) {
    CONFIRMED("CONFIRMED"),
    IN_KITCHEN_RIDER_NOT_ASSIGNED("IN_KITCHEN_RIDER_NOT_ASSIGNED"),
    IN_KITCHEN_RIDER_ASSIGNED("IN_KITCHEN_RIDER_ASSIGNED"),
    IN_KITCHEN_RIDER_ARRIVED("IN_KITCHEN_RIDER_ARRIVED"),
    ON_THE_WAY("ON_THE_WAY"),
    DELIVERED("DELIVERED")
}

data class OrderDetailsItem(
    val id: Long,
    val restaurantId: Long,
    val status: OrderStatus,
    val restaurantName: String,
    val estimatedTime: String,
    val estimatedTimeDesc: String,
    val statusDesc: String,
    val restaurantMapData: MapMarker?,
    val destinationMapData: MapMarker?,
    val riderMapData: MapMarker?,
    val progressPadding: Float,
)