package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.MemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class MemberRepository {
    private val service: MemberService = RestApiClient.memberService

    suspend fun isAvailableNickName(nickName: String, customerId: String): Boolean {
        val response: RestApiResponse<NickName>?
        try {
            response = service.isAvailableNickName(NickNameBody(nickName, customerId))
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.isAvailableNickName error!")
        }
        return when {
            response.isSuccess() -> {
                true
            }
            else -> {
                throw RequestFailException(response.code, response.msg)
            }
        }
    }

    suspend fun setNickName(nickName: String, auth: String, customerId: String): Boolean {
        val response: RestApiResponse<NickName>
        try {
            response = service.setNickName(auth, NickNameBody(nickName, customerId))
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.setNickName error!")
        }

        return when {
            response.isSuccess() -> {
                true
            }
            else -> {
                throw RequestFailException(response.code, response.msg)
            }
        }
    }

    suspend fun signupCheck(firebaseUuid: String): MemberSignupCheck {
        val response: RestApiResponse<MemberSignupCheck>
        try {
            response = service.signupCheck(MemberSignupCheckBody(firebaseUuid))
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.signupCheck error!")
        }

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!
    }


    suspend fun setBasicProfile(auth: String): Upload {
        val response: RestApiResponse<Upload>
        try {
            response = service.setBasicProfile(auth)
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.setBasicProfile error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!
    }

    suspend fun getReportType(auth: String): List<ReportType> {
        val response: RestApiResponse<ReportTypeList>
        try {
            response = service.getReportType(auth)
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.getReportType error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!.list
    }

    suspend fun setReport(auth: String, reportMid: String, reportTypeId: String): Boolean {
        val response: RestApiResponse<Void>?
        try {
            response = service.setReport(auth, MemberReportBody(reportMid, reportTypeId))
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.setReport error!")
        }
        return when {
            response.isSuccess() -> {
                true
            }
            else -> {
                throw RequestFailException(response.code, response.msg)
            }
        }
    }

    suspend fun deleteBlock(auth: String, blockMid: String): ArrayList<String> {
        val response: RestApiResponse<BlockList>
        try {
            response = service.deleteBlock(auth, blockMid)
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.deleteBlock error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!.mIds
    }

    suspend fun addBlock(auth: String, blockMid: String): ArrayList<String> {
        val response: RestApiResponse<BlockList>
        try {
            response = service.addBlock(auth, blockMid)
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.addBlock error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!.mIds
    }


}