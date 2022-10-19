package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.UserCount
import com.coinlive.chat.api.service.ChannelService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

object ChannelRepository {
    private val service: ChannelService = RestApiClient.channelService

    fun getUserCount(coinId:String, auth:String) : UserCount {
        val response = service.getUserCount(coinId,auth).execute().body()
            ?: throw NetworkException("ChannelRepository.getUserCount error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("ChannelRepository.getUserCount fail. please check coinId or token",response.code,response.msg)
        }
        return response.d!!
    }

    fun userJoin(coinId:String, auth:String) : UserCount {
        val response = service.userJoin(coinId,auth).execute().body()
            ?: throw NetworkException("ChannelRepository.userJoin error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("ChannelRepository.userJoin fail. please check coinId or token",response.code,response.msg)
        }
        return response.d!!
    }

    fun userLeave(coinId:String, auth:String) : UserCount {
        val response = service.userLeave(coinId,auth).execute().body()
            ?: throw NetworkException("ChannelRepository.userLeave error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("ChannelRepository.userLeave fail. please check coinId or token",response.code,response.msg)
        }
        return response.d!!
    }

}