package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChattingMemberService {
    /*
    /v1/chatting-member/channel [GET] {auth - API Key} // 해당 Customer 채널 조회
    /v1/chatting-member/customer/info [GET] {auth - API Key} // Customer 정보 조회
    /v1/chatting-member/info [GET] {auth - customer token} // 유저 정보 조회
    /v1/chatting-member/sign-up [POST] {auth - customer token} // 유저 회원가입
    /v1/chatting-member/token [POST] {auth - API Key} // 유저 Custom token 발급
     */

    @GET("v1/chatting-member/channel")
    fun getChannelList(@Header("Authorization") apiKey: String): Call<RestApiResponse<ChannelList>>

    @GET("v1/chatting-member/customer/info")
    fun getCustomerInfo(@Header("Authorization") apiKey: String): Call<RestApiResponse<Customer>>

    @GET("v1/chatting-member/my/info")
    fun getCustomerMemberInfo(@Header("Authorization") auth: String): Call<RestApiResponse<CustomerUser>>

    @POST("v1/chatting-member/sign-up")
    fun customerUserSingUp(
        @Header("Authorization") auth: String,
        @Body user: CustomerUserSignUpBody
    ): Call<RestApiResponse<CustomerUserSignUpResponse>>

    @POST("v1/chatting-member/token")
    fun getCustomToken(
        @Header("Authorization") apiKey: String,
        @Body user: CustomTokenBody
    ): Call<RestApiResponse<CustomerUserSignUpResponse>>
}