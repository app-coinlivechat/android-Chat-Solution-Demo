package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.ChattingMemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException
import com.coinlive.chat.firebase.service.Authentication

object ChattingMemberRepository {
    private val service: ChattingMemberService = RestApiClient.chattingMemberService

    fun getChannelList(apiKey: String): List<Channel> {
        val response = service.getChannelList(apiKey).execute().body()
            ?: throw NetworkException("getChannelList error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException("getChannelList fail. please check apiKey", response.code, response.msg)
        }
        return response.d!!.list
    }

    fun getCustomerInfo(apiKey: String): Customer {
        val response = service.getCustomerInfo(apiKey).execute().body()
            ?: throw NetworkException("getCustomerInfo error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException("getCustomerInfo fail. please check apiKey", response.code, response.msg)
        }
        return response.d!!
    }


    fun getCustomerMemberInfo(auth: String): CustomerUser {
        val response = service.getCustomerMemberInfo(auth).execute().body()
            ?: throw NetworkException("getCustomerMemberInfo error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException("getCustomerMemberInfo fail. please check token", response.code, response.msg)
        }
        return response.d!!
    }

    /**
     *
     */
    fun customerUserSignUp(auth: String, user: CustomerUserSignUpBody): CustomerUserSignUpResponse {
        val response = service.customerUserSingUp(auth, user).execute().body()
            ?: throw NetworkException("customerUserSingUp error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "customerUserSingUp fail. please check customToken or customerUserSignUpBody",
                response.code,
                response.msg
            )
        }
        return response.d!!
    }


    /**
     * 사용자를 Firebase Authentication에 로그인하고 coinlive에 가입하기 위해 Firebase Authentication [CustomerUserSignUpResponse]을
     * 전달 받습니다.
     * @param[apiKey] coinlive에서 전달 받은 customerApiKey
     * @param[uuid] 사용자 uuid
     * @return[CustomerUserSignUpResponse] 사용자 uuid를 이용하여 Firebase Authentication에 로그인 할 수 있는 정보를 전달합니다.
     * 전달받은 데이터를 이용하여 [Authentication.signIn] 또는 [customerUserSignUp]을 이용하는데 사용하십시요.
     */
    fun getCustomToken(apiKey: String, uuid: String): CustomerUserSignUpResponse {
        val response = service.getCustomToken(apiKey, CustomTokenBody(uuid)).execute().body()
            ?: throw NetworkException("getCustomToken error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException("getCustomToken fail. please check apiKey or uuid", response.code, response.msg)
        }
        return response.d!!
    }


}