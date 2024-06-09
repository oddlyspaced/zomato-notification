package com.oddlyspaced.zomato.notification.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author : hardik
 * @created : 09/06/24, Sunday
 **/
data class OrderHistoryResponse(
    val status: String,
    val results: List<OrderHistoryResult>
)

data class OHLayoutConfig(
    @SerializedName("snippet_type")
    val snippetType: String?,
    @SerializedName("layout_type")
    val layoutType: String?,
    @SerializedName("section_count")
    val sectionCount: Int?,
)

data class ClickActionDeeplink(
    @SerializedName("url")
    val url: String,
)

data class ClickAction(
    @SerializedName("type")
    val type: String,
    @SerializedName("deeplink")
    val deeplink: ClickActionDeeplink,
)

data class ContainerTitle(
    val text: String,
)

data class Container(
    val title: ContainerTitle,
    val tag: Container?
)

data class OrderHistorySnippet(
    @SerializedName("click_action")
    val clickAction: ClickAction,
    @SerializedName("top_container")
    val topContainer: Container,
    @SerializedName("bottom_container")
    val bottomContainer: Container,
)

data class OrderHistoryResult(
    @SerializedName("layout_config")
    val layoutConfig: OHLayoutConfig,
    @SerializedName("order_history_snippet_type_2")
    val orderHistorySnippet: OrderHistorySnippet?,
)