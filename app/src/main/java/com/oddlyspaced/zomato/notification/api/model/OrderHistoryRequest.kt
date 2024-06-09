package com.oddlyspaced.zomato.notification.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author : hardik
 * @created : 09/06/24, Sunday
 **/

class OHRAdditionalFilters
data class OHRLocation(
    @SerializedName("entity_type") var entityType: String = "subzone",
    @SerializedName("entity_id") var entityId: String = "115333",
    @SerializedName("place_type") var placeType: String = "GOOGLE_PLACE",
    @SerializedName("place_id") var placeId: String = "ChIJN8KDNnq7j4AR18Xh9LVB4c4",
    @SerializedName("place_name") var placeName: String = "Mountain View, United States",
    @SerializedName("cell_id") var cellId: String = "9263827477350842368",
    @SerializedName("city_id") var cityId: String = "10847",
    @SerializedName("address_id") var addressId: String = "0",
    @SerializedName("current_poi_id") var currentPoiId: String = "0",
    @SerializedName("current_poi_name") var currentPoiName: String = "",
    @SerializedName("poi_status") var poiStatus: String = ""
)

class OHROrderScheduling
data class OHRAppContextualParams(
    @SerializedName("order_scheduling") var orderScheduling: OHROrderScheduling = OHROrderScheduling()
)

data class OrderHistoryRequest(
    @SerializedName("request_type") var requestType: String = "default",
    @SerializedName("additional_filters") var additionalFilters: OHRAdditionalFilters = OHRAdditionalFilters(),
    @SerializedName("page_index") var pageIndex: String = "1",
    @SerializedName("count") var count: Int = 10,
    @SerializedName("location") var location: OHRLocation = OHRLocation(),
    @SerializedName("is_gold_mode_on") var isGoldModeOn: Boolean = false,
    @SerializedName("keyword") var keyword: String = "",
    @SerializedName("load_more") var loadMore: Boolean = false,
    @SerializedName("app_contextual_params") var appContextualParams: OHRAppContextualParams = OHRAppContextualParams()
)