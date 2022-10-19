
package com.coinlive.chat.api

import com.coinlive.chat.BuildConfig
import com.coinlive.chat.api.service.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RestApiClient {
    private val baseUrl = if(BuildConfig.DEBUG) "https://dev.coinlivechat.com/" else "https://api.coinlivechat.com/"
    val coinService:CoinService
    val channelService:ChannelService
    val chattingMemberService:ChattingMemberService
    val memberService:MemberService
    val notificationService:NotificationService
    val uploadService:UploadService
    init {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(if(BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else
                HttpLoggingInterceptor.Level.NONE))
            .addInterceptor(Interceptor {
                val request:Request = it.request().newBuilder().addHeader("Accept-Language","ko").build()
                it.proceed(request)
            })
            .build()

        val retrofit:Retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(client).build()

        coinService = retrofit.create(CoinService::class.java)
        channelService = retrofit.create(ChannelService::class.java)
        chattingMemberService = retrofit.create(ChattingMemberService::class.java)
        memberService = retrofit.create(MemberService::class.java)
        notificationService = retrofit.create(NotificationService::class.java)
        uploadService = retrofit.create(UploadService::class.java)
    }






}