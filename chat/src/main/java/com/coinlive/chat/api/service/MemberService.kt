package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.*
import retrofit2.http.*

interface MemberService {
    @GET("v1/member/check/nickname")
    suspend fun isAvailableNickName(@Body nickName: NickNameBody): RestApiResponse<NickName>

    @PUT("v1/member/nickname")
    suspend fun setNickName(
        @Header("Authorization") auth: String,
        @Body nickName: NickNameBody,
    ): RestApiResponse<NickName>

    @POST("v1/member/check")
    suspend fun signupCheck(@Body firebaseUuid: MemberSignupCheckBody): RestApiResponse<MemberSignupCheck>

    @PUT("v1/member/profile/basic")
    suspend fun setBasicProfile(@Header("Authorization") auth: String): RestApiResponse<Upload>

    @GET("v1/member/report/type")
    suspend fun getReportType(@Header("Authorization") auth: String): RestApiResponse<ReportTypeList>

    @POST("v1/member/report")
    suspend fun setReport(@Header("Authorization") auth: String, @Body body: MemberReportBody): RestApiResponse<Void>

    @DELETE("v1/member/{bMid}/block")
    suspend fun deleteBlock(
        @Header("Authorization") auth: String,
        @Path("bMid") blockMid: String,
    ): RestApiResponse<BlockList>

    @POST("v1/member/{bMid}/block")
    suspend fun addBlock(
        @Header("Authorization") auth: String,
        @Path("bMid") blockMid: String,
    ): RestApiResponse<BlockList>

}