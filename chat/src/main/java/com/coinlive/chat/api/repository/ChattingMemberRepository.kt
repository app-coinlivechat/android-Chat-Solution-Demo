package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.ChattingMemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class ChattingMemberRepository {
    private val service: ChattingMemberService = RestApiClient.chattingMemberService

    suspend fun getChannelList(apiKey: String): List<Channel> {
        try {
            val response = service.getChannelList(apiKey)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException("getChannelList fail. please check apiKey", response.code, response.msg)
            }
            return response.d!!.list
        } catch (exception: Exception) {
            throw NetworkException("getCustomerInfo error!")
        }
    }

    suspend fun getCustomerInfo(apiKey: String): Customer {
        try {
            val response = service.getCustomerInfo(apiKey)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException("getCustomerInfo fail. please check apiKey", response.code, response.msg)
            }
            return response.d!!
        } catch (exception: Exception) {
            throw NetworkException("getCustomerInfo error!")
        }
    }

    suspend fun getCustomerMemberInfo(auth: String): CustomerUser {
        try {
            val response = service.getCustomerMemberInfo(auth)

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException("getCustomerMemberInfo fail. please check token",
                    response.code,
                    response.msg)
            }
            return response.d!!
        } catch (exception: Exception) {
            throw NetworkException("getCustomerMemberInfo error!")
        }
    }

    //    suspend fun customerUserSignUp(auth: String, user: CustomerUserSignUpBody): CustomerUserSignUp {
    suspend fun customerUserSignUp(auth: String, user: CustomerUserSignUpBody): RestApiResponse<CustomerUserSignUp> {
        try {
            return service.customerUserSignUp(auth, user)

//            return when {
//                response.isSuccess() -> {
//                    true
//                }
//                else -> {
//                    LoggerHelper.de(
//                        "customerUserSingUp fail. please check customToken or customerUserSignUpBody ${response.code}, ${response.msg}")
//                    false
//                }
//            }
        } catch (exception: Exception) {
            throw NetworkException("customerUserSingUp error!")
        }
    }

    suspend fun getCustomToken(apiKey: String, uuid: String): CustomerUserSignUp {
        try {
            val response = service.getCustomToken(apiKey, CustomTokenBody(uuid))
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException("getCustomToken fail. please check apiKey or uuid",
                    response.code,
                    response.msg)
            }
            return response.d!!
        } catch (exception: Exception) {
            throw NetworkException("getCustomToken error!")
        }

    }


}