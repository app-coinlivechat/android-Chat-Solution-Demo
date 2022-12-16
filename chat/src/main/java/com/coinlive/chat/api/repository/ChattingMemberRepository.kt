package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.ChattingMemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class ChattingMemberRepository {
    private val service: ChattingMemberService = RestApiClient.chattingMemberService

    suspend fun getChannelList(customerName: String): List<Channel> {
        val response: RestApiResponse<ChannelList>
        try {
            response = service.getChannelList(customerName)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw NetworkException("getChannelList error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!.list
    }

    suspend fun getCustomerInfo(customerName: String): Customer {
        val response: RestApiResponse<Customer>
        try {
            response = service.getCustomerInfo(customerName)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw NetworkException("getCustomerInfo error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!
    }

    suspend fun getCustomerMemberInfo(auth: String): CustomerUser {
        val response: RestApiResponse<CustomerUser>
        try {
            response = service.getCustomerMemberInfo(auth)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw NetworkException("getCustomerMemberInfo error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!
    }

    suspend fun customerUserSignUp(auth: String, user: CustomerUserSignUpBody): Boolean {
        val response: RestApiResponse<CustomerUserSignUp>
        try {
            response = service.customerUserSignUp(auth, user)
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw NetworkException("getCustomerMemberInfo error!")
        }
        if (!response.isSuccess()) {
            throw RequestFailException(response.code, response.msg)
        }
        return true

    }

    suspend fun getCustomToken(password: String,customerName: String, uuid: String): CustomerUserSignUp {
        val response: RestApiResponse<CustomerUserSignUp>
        try {
            response = service.getCustomToken(CustomTokenBody(uuid,customerName,password))

        } catch (exception: Exception) {
            exception.printStackTrace()
            throw NetworkException("getCustomToken error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!

    }


}