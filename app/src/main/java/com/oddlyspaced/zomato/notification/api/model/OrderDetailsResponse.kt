package com.oddlyspaced.zomato.notification.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author : hardik
 * @created : 10/06/24, Monday
 **/

data class OrderDetailsResponse(
    val response: OrderDetails
)

data class OrderDetailsObj(
    @SerializedName("tab_id")
    val tagId: Long,
    @SerializedName("res_id")
    val resId: Long,
    val status: String,
    @SerializedName("res_name")
    val restaurantName: String,
)

data class PillDataItemTitle(
    val text: String,
)

data class PillDataItem(
    val title: PillDataItemTitle,
)

data class PillData(
    @SerializedName("left_data")
    val leftData: PillDataItem,
    @SerializedName("right_data")
    val rightData: PillDataItem,
)

data class SubtitleData(
    val text: String,
)

data class HeaderData(
    @SerializedName("pill_data")
    val pillData: PillData?,
    val subtitle2: SubtitleData?,
)

data class MapMarker(
    val longitude: Float,
    val latitude: Float,
    val type: String,
)

data class MapData(
    val markers: List<MapMarker>
)

data class OrderDetails(
    @SerializedName("order_details")
    val orderDetails: OrderDetailsObj,
    @SerializedName("header_data")
    val headerData: HeaderData,
    @SerializedName("map_data")
    val mapData: MapData?,
)