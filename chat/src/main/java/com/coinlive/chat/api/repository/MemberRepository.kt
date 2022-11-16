package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.MemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException
import com.coinlive.chat.util.LoggerHelper

class MemberRepository {
    private val service: MemberService = RestApiClient.memberService

    fun isAvailableNickName(nickName: String, customerId:String): String {
        val response = service.isAvailableNickName(NickNameBody(nickName,customerId)).execute().body()
            ?: throw NetworkException("MemberRepository.isAvailableNickName error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.isAvailableNickName fail. please check nickName or customerId",
                response.code, response.msg
            )
        }
        return response.d!!.word
    }

    fun setNickName(nickName: String, auth: String, customerId:String): String {
        val response = service.setNickName(auth, NickNameBody(nickName, customerId)).execute().body()
            ?: throw NetworkException("MemberRepository.setNickName error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.setNickName fail. please check nickName or auth, customerId",
                response.code, response.msg
            )
        }
        return response.d!!.word
    }

    suspend fun signupCheck(firebaseUuid: String): MemberSignupCheck {
        try {
            val response = service.signupCheck(MemberSignupCheckBody(firebaseUuid))
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "MemberRepository.signupCheck fail. please check firebaseUuid",
                    response.code, response.msg
                )
            }
            return response.d!!
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.signupCheck error!")
        }
    }


    fun setBasicProfile(auth: String): Upload {
        val response = service.setBasicProfile(auth).execute().body()
            ?: throw NetworkException("MemberRepository.setBasicProfile error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.setBasicProfile fail. please check auth",
                response.code, response.msg
            )
        }
        return response.d!!
    }

    fun getReportType(auth: String): List<ReportType> {
        val response = service.getReportType(auth).execute().body()
            ?: throw NetworkException("MemberRepository.getReportType error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.getReportType fail. please check auth",
                response.code, response.msg
            )
        }
        return response.d!!.list
    }

    fun setReport(auth: String, reportMid: String,reportTypeId: String): Boolean {
        val response = service.setReport(auth, MemberReportBody(reportMid,reportTypeId)).execute().body()
            ?: throw NetworkException("MemberRepository.setReport error!")

        return when {
            response.isSuccess() -> {
                true
            }
            else -> {
                throw RequestFailException("MemberRepository.setReport fail. please check reportMid,reportTypeId or auth",
                    response.code,
                    response.msg)
            }
        }
    }

    fun deleteBlock(auth: String, blockMid: String): List<String> {
        val response = service.deleteBlock(auth, blockMid).execute().body()
            ?: throw NetworkException("MemberRepository.deleteBlock error!")
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.deleteBlock fail. please check blockMid or auth",
                response.code, response.msg
            )
        }
        return response.d!!.mIds
    }

    fun addBlock(auth: String, blockMid: String): List<String> {
        val response = service.addBlock(auth, blockMid).execute().body()
            ?: throw NetworkException("MemberRepository.addBlock error!")
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.addBlock fail. please check blockMid or auth",
                response.code, response.msg
            )
        }
        return response.d!!.mIds
    }



}