package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.UserCount
import com.coinlive.chat.api.model.RestApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ChannelService {

    @GET("v1/chat/{cid}/count")
    suspend fun getUserCount(
        @Path("cid") coinId: String,
        @Query("mid") firebaseUuid : String?,
        @Header("Authorization") auth: String
    ): RestApiResponse<UserCount>

}