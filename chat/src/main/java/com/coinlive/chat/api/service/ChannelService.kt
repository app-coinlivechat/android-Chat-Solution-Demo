package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.UserCount
import com.coinlive.chat.api.model.RestApiResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChannelService {
    /*
    /v1/chat/{cid}/count [GET] {auth} // 채팅 사용자 수 조회
    /v1/chat/{cid}/join [POST] {auth} // 채팅 조인
    /v1/chat/{cid}/leave [DELETE] {auth} // 채팅 아웃
     */

    @GET("v1/chat/{cid}/count")
    fun getUserCount(
        @Path("cid") coinId: String,
        @Header("Authorization") auth: String
    ): Call<RestApiResponse<UserCount>>

    @POST("v1/chat/{cid}/join")
    fun userJoin(@Path("cid") coinId: String, @Header("Authorization") auth: String): Call<RestApiResponse<UserCount>>

    @DELETE("v1/chat/{cid}/leave")
    fun userLeave(@Path("cid") coinId: String, @Header("Authorization") auth: String): Call<RestApiResponse<UserCount>>

}