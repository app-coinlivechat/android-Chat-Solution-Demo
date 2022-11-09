package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.NotificationBody
import com.coinlive.chat.api.model.NotificationMap
import com.coinlive.chat.api.model.NotificationTypeList
import com.coinlive.chat.api.model.RestApiResponse
import retrofit2.Call
import retrofit2.http.*

interface NotificationService {
    /*
    /v2/notification [POST] // 노티 설정, [DELETE] // 노티 해제
    /v2/notification/type [GET] // 채팅 알림 설정 타입 조회
     */
    @POST("v2/notification")
    fun setNotification(
        @Header("Authorization") auth: String,
        @Body body: NotificationBody
    ): Call<RestApiResponse<Void>>

    @DELETE("v2/notification")
    fun deleteNotification(@Header("Authorization") auth: String, @Body body: NotificationBody):
            Call<RestApiResponse<Void>>

    @GET("v2/notification/type")
    fun getNotificationType(@Path("cid") coinId: String, @Header("Authorization") auth: String):
            Call<RestApiResponse<NotificationTypeList>>

    @GET("v1/notification/{cid}/setting")
    suspend fun getNotificationSetting(@Path("cid") coinId: String, @Header("Authorization") auth: String):
            RestApiResponse<NotificationMap>
}