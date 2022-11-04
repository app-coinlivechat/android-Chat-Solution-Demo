package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ChattingMemberService {
    @GET("v1/chatting-member/channel")
    suspend fun getChannelList(@Header("Authorization") apiKey: String): RestApiResponse<ChannelList>

    @GET("v1/chatting-member/customer/info")
    suspend fun getCustomerInfo(@Header("Authorization") apiKey: String): RestApiResponse<Customer>

    @GET("v1/chatting-member/my/info")
    suspend fun getCustomerMemberInfo(@Header("Authorization") auth: String): RestApiResponse<CustomerUser>

    @POST("v1/chatting-member/sign-up")
    suspend fun customerUserSignUp(
        @Header("Authorization") auth: String,
        @Body user: CustomerUserSignUpBody
    ): RestApiResponse<CustomerUserSignUp>

    @POST("v1/chatting-member/token")
    suspend fun getCustomToken(
        @Header("Authorization") apiKey: String,
        @Body user: CustomTokenBody
    ): RestApiResponse<CustomerUserSignUp>
}