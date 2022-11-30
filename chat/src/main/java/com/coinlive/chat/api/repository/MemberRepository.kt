package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.MemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class MemberRepository {
    private val service: MemberService = RestApiClient.memberService

    suspend fun isAvailableNickName(nickName: String, customerId: String): String {
        try {
            val response = service.isAvailableNickName(NickNameBody(nickName, customerId))

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "MemberRepository.isAvailableNickName fail. please check nickName or customerId",
                    response.code, response.msg
                )
            }
            return response.d!!.word
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.isAvailableNickName error!")
        }
    }

    suspend fun setNickName(nickName: String, auth: String, customerId: String): String {
        try {
            val response = service.setNickName(auth, NickNameBody(nickName, customerId))

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "MemberRepository.setNickName fail. please check nickName or auth, customerId",
                    response.code, response.msg
                )
            }
            return response.d!!.word
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.setNickName error!")
        }
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


    suspend fun setBasicProfile(auth: String): Upload {
        try {
            val response = service.setBasicProfile(auth)

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "MemberRepository.setBasicProfile fail. please check auth",
                    response.code, response.msg
                )
            }
            return response.d!!
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.setBasicProfile error!")
        }
    }

    suspend fun getReportType(auth: String): List<ReportType> {
        try {
            val response = service.getReportType(auth)

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "MemberRepository.getReportType fail. please check auth",
                    response.code, response.msg
                )
            }
            return response.d!!.list
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.getReportType error!")
        }
    }

    suspend fun setReport(auth: String, reportMid: String, reportTypeId: String): Boolean {
        try {
            val response = service.setReport(auth, MemberReportBody(reportMid, reportTypeId))
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
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.setReport error!")
        }
    }

    suspend fun deleteBlock(auth: String, blockMid: String): ArrayList<String> {
        try {
            val response = service.deleteBlock(auth, blockMid)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "MemberRepository.deleteBlock fail. please check blockMid or auth",
                    response.code, response.msg
                )
            }
            return response.d!!.mIds
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.deleteBlock error!")
        }
    }

    suspend fun addBlock(auth: String, blockMid: String): ArrayList<String> {
        try {
            val response = service.addBlock(auth, blockMid)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "MemberRepository.addBlock fail. please check blockMid or auth",
                    response.code, response.msg
                )
            }
            return response.d!!.mIds
        } catch (exception: Exception) {
            throw NetworkException("MemberRepository.addBlock error!")
        }
    }


}