package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.NotificationBody
import com.coinlive.chat.api.model.NotificationMap
import com.coinlive.chat.api.model.NotificationTypeList
import com.coinlive.chat.api.model.RestApiResponse
import retrofit2.http.*

interface NotificationService {
    /*
    /v2/notification [POST] // 노티 설정, [DELETE] // 노티 해제
    /v2/notification/type [GET] // 채팅 알림 설정 타입 조회
     */
    @POST("v2/notification")
    suspend fun setNotification(
        @Header("Authorization") auth: String,
        @Body body: NotificationBody,
    ): RestApiResponse<Void>

    @DELETE("v2/notification")
    suspend fun deleteNotification(@Header("Authorization") auth: String, @Body body: NotificationBody):
            RestApiResponse<Void>

    @GET("v2/notification/type")
    suspend fun getNotificationType(@Header("Authorization") auth: String):
            RestApiResponse<NotificationTypeList>

    @GET("v1/notification/{cid}/setting")
    suspend fun getNotificationSetting(@Path("cid") coinId: String, @Header("Authorization") auth: String):
            RestApiResponse<NotificationMap>
}