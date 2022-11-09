package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.Coin
import com.coinlive.chat.api.service.CoinService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException


class CoinRepository {
    private val service: CoinService = RestApiClient.coinService

    suspend fun getCoin(coinId: String, auth: String): Coin {
        try{
            val response = service.getCoin(coinId, auth)

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "CoinRepository.getCoin fail. please check coinId or token",
                    response.code,
                    response.msg
                )
            }
            return response.d!!
        }catch (exception:Exception) {
            throw NetworkException("CoinRepository.getCoin error!")
        }
    }
}