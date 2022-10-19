package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.Coin
import com.coinlive.chat.api.service.CoinService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException



object CoinRepository{
    private val service:CoinService = RestApiClient.coinService

    fun getCoin(coinId: String, auth:String): Coin {
        val response = service.getCoin(coinId,auth).execute().body()
            ?: throw NetworkException("CoinRepository.getCoin error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("CoinRepository.getCoin fail. please check coinId or token",response.code,response.msg)
        }
        return response.d!!
    }
}