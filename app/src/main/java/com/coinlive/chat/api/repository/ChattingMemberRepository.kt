package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.ChattingMemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

object ChattingMemberRepository {
    private val service:ChattingMemberService = RestApiClient.chattingMemberService

    fun getChannelList(apiKey:String) : List<Channel> {
        val response = service.getChannelList(apiKey).execute().body()
            ?: throw NetworkException("getChannelList error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("getChannelList fail. please check apiKey",response.code,response.msg)
        }
        return response.d!!.list
    }

    fun getCustomerInfo(apiKey:String) : Customer {
        val response = service.getCustomerInfo(apiKey).execute().body()
            ?: throw NetworkException("getCustomerInfo error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("getCustomerInfo fail. please check apiKey",response.code,response.msg)
        }
        return response.d!!
    }


    fun getCustomerMemberInfo(auth:String) : CustomerUser {
        val response = service.getCustomerMemberInfo(auth).execute().body()
            ?: throw NetworkException("getCustomerMemberInfo error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("getCustomerMemberInfo fail. please check token",response.code,response.msg)
        }
        return response.d!!
    }


    fun customerUserSingUp(customToken:String, user: CustomerUserSignUpBody) :CustomerUserSignUpResponse {
        val response = service.customerUserSingUp(customToken, user).execute().body()
            ?: throw NetworkException("customerUserSingUp error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("customerUserSingUp fail. please check customToken or customerUserSignUpBody",response.code,response.msg)
        }
        return response.d!!
    }


    fun getCustomToken(apiKey:String, uuid:String) :CustomerUserSignUpResponse {
        val response = service.getCustomToken(apiKey, CustomTokenBody(uuid)).execute().body()
            ?: throw NetworkException("getCustomToken error!")

        if(!response.isSuccess() && response.d == null){
            throw RequestFailException("getCustomToken fail. please check apiKey or uuid",response.code,response.msg)
        }
        return response.d!!
    }


}