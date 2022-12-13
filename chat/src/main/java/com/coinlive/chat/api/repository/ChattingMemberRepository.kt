package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.ChattingMemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class ChattingMemberRepository {
    private val service: ChattingMemberService = RestApiClient.chattingMemberService

    suspend fun getChannelList(customerName: String): List<Channel> {
        try {
            val response = service.getChannelList(customerName)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException("getChannelList fail. please check apiKey", response.code, response.msg)
            }
            return response.d!!.list
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw NetworkException("getCustomerInfo error!")
        }
    }

    suspend fun getCustomerInfo(customerName: String): Customer {
        try {
            val response = service.getCustomerInfo(customerName)
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

    suspend fun customerUserSignUp(auth: String, user: CustomerUserSignUpBody): RestApiResponse<CustomerUserSignUp> {
        try {
            return service.customerUserSignUp(auth, user)
        } catch (exception: Exception) {
            throw NetworkException("customerUserSingUp error!")
        }
    }

    suspend fun getCustomToken(password: String,customerName: String, uuid: String): CustomerUserSignUp {
        try {
            val response = service.getCustomToken(CustomTokenBody(uuid,customerName,password))
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