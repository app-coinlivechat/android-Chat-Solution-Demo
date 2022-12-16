package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.RestApiResponse
import com.coinlive.chat.api.model.UserCount
import com.coinlive.chat.api.service.ChannelService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class ChannelRepository {
    private val service: ChannelService = RestApiClient.channelService

    suspend fun getUserCount(coinId: String,firebaseUuid : String?, auth: String): UserCount {
        val response: RestApiResponse<UserCount>
        try {
            response = service.getUserCount(coinId,firebaseUuid, auth)
        } catch (exception: Exception) {
            throw NetworkException("ChannelRepository.getUserCount error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!
    }

}