package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.UserCount
import com.coinlive.chat.api.service.ChannelService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class ChannelRepository {
    private val service: ChannelService = RestApiClient.channelService

    suspend fun getUserCount(coinId: String, auth: String): UserCount {
        try {
            val response = service.getUserCount(coinId, auth)

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "ChannelRepository.getUserCount fail. please check coinId or token",
                    response.code,
                    response.msg
                )
            }
            return response.d!!
        } catch (exception: Exception) {
            throw NetworkException("ChannelRepository.getUserCount error!")
        }
    }

}