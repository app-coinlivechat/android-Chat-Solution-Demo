package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.NotificationBody
import com.coinlive.chat.api.model.NotificationMap
import com.coinlive.chat.api.model.NotificationTypeList
import com.coinlive.chat.api.model.RestApiResponse
import retrofit2.http.*

interface NotificationService {

    @POST("v2/notification")
    suspend fun setNotification(
        @Header("Authorization") auth: String,
        @Body body: NotificationBody,
    ): RestApiResponse<Void>

    @HTTP(method = "DELETE", path = "v2/notification", hasBody = true)
    suspend fun deleteNotification(
        @Header("Authorization") auth: String,
        @Body body: NotificationBody,
    ): RestApiResponse<Void>

    @GET("v2/notification/type")
    suspend fun getNotificationType(@Header("Authorization") auth: String):
            RestApiResponse<NotificationTypeList>

    @GET("v1/notification/{cid}/setting")
    suspend fun getNotificationSetting(@Path("cid") coinId: String, @Header("Authorization") auth: String):
            RestApiResponse<NotificationMap>
}