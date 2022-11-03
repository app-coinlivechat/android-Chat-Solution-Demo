package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.RestApiResponse
import com.coinlive.chat.api.model.Coin
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CoinService {
    @GET("v1/coin/{cid}")
    fun getCoin(@Path("cid") coinId: String, @Header("Authorization") auth: String): Call<RestApiResponse<Coin>>
}