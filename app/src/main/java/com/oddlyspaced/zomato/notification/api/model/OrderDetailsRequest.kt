package com.oddlyspaced.zomato.notification.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author : hardik
 * @created : 10/06/24, Monday
 **/

data class OrderDetailsRequest(
    @SerializedName("postback_params")
    val postbackParams: PostbackParams? = null,
    @SerializedName("has_explorer_ended")
    val hasExplorerEnded: Int = 0,
)

class PostbackParams