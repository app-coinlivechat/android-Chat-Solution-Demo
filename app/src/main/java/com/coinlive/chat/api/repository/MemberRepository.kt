package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.MemberService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

object MemberRepository {
    private val service: MemberService = RestApiClient.memberService

    fun isAvailableNickName(nickName: String): String {
        val response = service.isAvailableNickName(NickNameBody(nickName)).execute().body()
            ?: throw NetworkException("MemberRepository.isAvailableNickName error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.isAvailableNickName fail. please check nickName",
                response.code, response.msg
            )
        }
        return response.d!!.word
    }

    fun setNickName(nickName: String, auth: String): String {
        val response = service.setNickName(auth, NickNameBody(nickName)).execute().body()
            ?: throw NetworkException("MemberRepository.setNickName error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.setNickName fail. please check nickName or auth",
                response.code, response.msg
            )
        }
        return response.d!!.nickName
    }

    fun signupCheck(firebaseUuid: String): MemberSignupCheck {
        val response = service.signupCheck(MemberSignupCheckBody(firebaseUuid)).execute().body()
            ?: throw NetworkException("MemberRepository.signupCheck error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.signupCheck fail. please check firebaseUuid",
                response.code, response.msg
            )
        }
        return response.d!!
    }

    fun getUserInfo(id: String, auth: String): User {
        val response = service.getUserInfo(auth, id).execute().body()
            ?: throw NetworkException("MemberRepository.getUserInfo error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.getUserInfo fail. please check id or auth",
                response.code, response.msg
            )
        }
        return response.d!!
    }

    fun setBasicProfile(auth: String): Upload {
        val response = service.setBasicProfile(auth).execute().body()
            ?: throw NetworkException("MemberRepository.setBasicProfile error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.setBasicProfile fail. please check id or auth",
                response.code, response.msg
            )
        }
        return response.d!!
    }

    fun getMyInfo(auth: String): MyInfo {
        val response = service.getMyInfo(auth).execute().body()
            ?: throw NetworkException("MemberRepository.getMyInfo error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "MemberRepository.getMyInfo fail. please check id or auth",
                response.code, response.msg
            )
        }
        return response.d!!
    }

}