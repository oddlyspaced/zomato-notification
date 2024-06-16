package com.oddlyspaced.zomato.notification.api

import com.oddlyspaced.zomato.notification.api.model.OrderDetailsRequest
import com.oddlyspaced.zomato.notification.api.model.OrderDetailsResponse
import com.oddlyspaced.zomato.notification.api.model.OrderHistoryRequest
import com.oddlyspaced.zomato.notification.api.model.OrderHistoryResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author : hardik
 * @created : 09/06/24, Sunday
 **/

/**
 * NOTE: The headers have been modified to not have any sensitive information present here. In case you want to run this project, you will need to update this file!
 */

interface Api {
    @Headers(
        "Host: api.zomato.com",
        "x-appsflyer-uid: XXXXXXXXXXXXX-XXXXXXXXXXXXXXXXXXX",
        "x-present-lat: 37.4218708",
        "x-user-defined-lat: 37.4218708",
        "x-jumbo-session-id: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXXXXXXXXXXX",
        "x-accessibility-voice-over-enabled: 0",
        "user-agent: &source=android_market&version=12&device_manufacturer=Google&device_brand=google&device_model=sdk_gphone64_arm64&api_version=818&app_version=v18.1.8",
        "x-device-language: en-US",
        "x-rider-installed: false",
        "x-zomato-client-id: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-access-token: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-present-long: -122.084455",
        "x-client-id: zomato_android_v2",
        "x-network-type: wifi",
        "x-zomato-uuid: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-app-language: &lang=en&android_language=en&android_country=",
        "x-firebase-instance-id: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        "x-device-pixel-ratio: 3.5",
        "x-o2-city-id: 10847",
        "x-android-id: XXXXXXXXXXXXXXXX",
        "x-zomato-app-version-code: XXXXXXXXXX",
        "accept: image/webp",
        "x-present-horizontal-accuracy: 5",
        "x-request-id: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-xtreme-installed: false",
        "x-zomato-app-version: 818",
        "x-city-id: 10847",
        "x-device-width: 1440",
        "pragma: akamai-x-get-request-id,akamai-x-cache-on, akamai-x-check-cacheable",
        "x-zomato-access-token: XXXXXX-XXXXXXXXXXXXXXXXXXX_-XXXXXXXXXXXXXXX.XXXXXXXXXXXXXXXXXXXXXXXXX_XXXXXXXXXXXXXXXXX",
        "x-vpn-active: 0",
        "x-zomato-refresh-token: XXXXXXXXXXXXXXXXXXXXXX-XXXXXXXXXXXXXXXXXXXX.XXXXX_XXXXXXXXXXXXXXXXXXXXXXXXX_XXXXX_XXXXX",
        "x-device-height: 2952",
        "x-user-defined-long: -122.084455",
        "x-blinkit-installed: false",
        "x-access-uuid: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-accessibility-dynamic-text-scale-factor: 1.0",
        "x-zomato-api-key: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        "x-zomato-is-metric: true",
        "user-bucket: 100",
        "user-high-priority: 4",
        "is-akamai-video-optimisation-enabled: 1",
        "x-app-theme: default",
        "content-type: application/json; charset=UTF-8",
        "priority: u=1, i"
    )
    @POST("gw/order/history/online_order")
    suspend fun getOrderHistory(@Body body: OrderHistoryRequest = OrderHistoryRequest()): OrderHistoryResponse

    @Headers(
        "Host: api.zomato.com",
        "x-appsflyer-uid: XXXXXXXXXXXXX-XXXXXXXXXXXXXXXXXXX",
        "x-present-lat: 37.4218708",
        "x-user-defined-lat: 37.4218708",
        "x-jumbo-session-id: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXXXXXXXXXXX",
        "x-accessibility-voice-over-enabled: 0",
        "user-agent: &source=android_market&version=12&device_manufacturer=Google&device_brand=google&device_model=sdk_gphone64_arm64&api_version=818&app_version=v18.1.8",
        "x-device-language: en-US",
        "x-rider-installed: false",
        "x-zomato-client-id: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-access-token: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-present-long: -122.084455",
        "x-client-id: zomato_android_v2",
        "x-network-type: wifi",
        "x-zomato-uuid: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-app-language: &lang=en&android_language=en&android_country=",
        "x-firebase-instance-id: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        "x-device-pixel-ratio: 3.5",
        "x-o2-city-id: 4",
        "x-android-id: XXXXXXXXXXXXXXXX",
        "x-zomato-app-version-code: XXXXXXXXXX",
        "accept: image/webp",
        "x-present-horizontal-accuracy: 5",
        "x-request-id: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "x-xtreme-installed: false",
        "x-zomato-app-version: 818",
        "x-city-id: 4",
        "x-device-width: 1440",
        "pragma: akamai-x-get-request-id,akamai-x-cache-on, akamai-x-check-cacheable",
        "x-zomato-access-token: XXXXXX-XXXXXXXXXXXXXXXXXXX_-XXXXXXXXXXXXXXX.XXXXXXXXXXXXXXXXXXXXXXXXX_XXXXXXXXXXXXXXXXX",
        "x-vpn-active: 0",
        "x-zomato-refresh-token: XXXXXXXXXXXXXXXXXXXXXX-XXXXXXXXXXXXXXXXXXXX.XXXXX_XXXXXXXXXXXXXXXXXXXXXXXXX_XXXXX_XXXXX",
        "x-device-height: 2952",
        "x-user-defined-long: -122.084455",
        "x-blinkit-installed: false",
        "x-accessibility-dynamic-text-scale-factor: 1.0",
        "x-zomato-api-key: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        "x-dv-token: XX_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX_XXXXXXXX",
        "x-zomato-is-metric: true",
        "user-bucket: 100",
        "user-high-priority: 4",
        "is-akamai-video-optimisation-enabled: 1",
        "x-app-theme: default",
        "content-type: application/x-www-form-urlencoded",
    )
    @POST("v2/order/crystal_v2")
    suspend fun getOrderDetails(
        @Query("tab_id") orderId: Long,
        @Query("is_push_permission_enabled") isPushPermissionEnabled: Int = 1,
        @Query("has_user_rated_current_version") hasUserRatedCurrentVersion: Int = 0,
        @Query("android_country") androidCountry: String = "",
        @Query("source") source: String = "default_source",
        @Query("lang") lang: String = "en",
        @Query("android_language") androidLanguage: String = "end",
        @Query("can_show_app_rating_blocker") canShowAppRatingBlocker: Int = 0,
        @Query("is_push_permission_asked") isPushPermissionAsked: Int = 1,
        @Query("city_id") cityId: Int = 4,
        @Body body: OrderDetailsRequest = OrderDetailsRequest()
    ): OrderDetailsResponse
}