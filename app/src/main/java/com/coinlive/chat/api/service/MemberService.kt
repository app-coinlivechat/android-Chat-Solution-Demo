package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.*
import retrofit2.Call
import retrofit2.http.*

interface MemberService {
    @GET("v1/member/check/nickname")
    fun isAvailableNickName(@Body nickName: NickNameBody): Call<RestApiResponse<NickName>>

    @PUT("v1/member/nickname")
    fun setNickName(
        @Header("Authorization") auth: String,
        @Body nickName: NickNameBody,
    ): Call<RestApiResponse<NickName>>

    @POST("v1/member/check")
    fun signupCheck(@Body firebaseUuid: MemberSignupCheckBody): Call<RestApiResponse<MemberSignupCheck>>

    @PUT("v1/member/profile/basic")
    fun setBasicProfile(@Header("Authorization") auth: String): Call<RestApiResponse<Upload>>

    @GET("v1/member/report/type")
    fun getReportType(@Header("Authorization") auth: String): Call<RestApiResponse<ReportTypeList>>

    @POST("v1/member/report")
    fun setReport(@Header("Authorization") auth: String, @Body body: MemberReportBody): Call<RestApiResponse<Void>>

    @DELETE("v1/member/{bMid}/block")
    fun deleteBlock(
        @Header("Authorization") auth: String,
        @Path("bMid") blockMid: String,
    ): Call<RestApiResponse<BlockList>>

    @POST("v1/member/{bMid}/block")
    fun addBlock(
        @Header("Authorization") auth: String,
        @Path("bMid") blockMid: String,
    ): Call<RestApiResponse<BlockList>>

}