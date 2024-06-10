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
    val tagId: Long
)

data class OrderDetails(
    @SerializedName("order_details")
    val orderDetails: OrderDetailsObj
)