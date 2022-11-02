package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.*
import retrofit2.Call
import retrofit2.http.*

interface MemberService {
    /*
    /v1/member/check/nickname [GET] // 닉네임 설정 여부
    /v1/member/nickname [PUT] // 닉네임 수정 {auth}
    /v1/member/check [POST] // 멤버 확인
    회원탈퇴 -> 운영으로 처리
    /v1/member/{mid}/info [GET] {auth} // mid 회원 정보 조회
    /v1/member/profile/basic [PUT] {auth} // 유저 프로필 기본 이미지 설정
    /v1/member/report/type [GET] {auth} // 신고 타입 조회
    /v1/member/report [POST] {auth} // 유저 신고
    /v1/member/{bMid}/block [Delete, post] {auth} //유저 차단 추가,삭제
     */

    @GET("v1/member/check/nickname")
    fun isAvailableNickName(@Body nickName: NickNameBody): Call<RestApiResponse<NickName>>

    @PUT("v1/member/nickname")
    fun setNickName(
        @Header("Authorization") auth: String,
        @Body nickName: NickNameBody,
    ): Call<RestApiResponse<NickName>>

    @POST("v1/member/check")
    fun signupCheck(@Body firebaseUuid: MemberSignupCheckBody): Call<RestApiResponse<MemberSignupCheck>>

    @PUT("v1/member/profile/basic")
    fun setBasicProfile(@Header("Authorization") auth: String): Call<RestApiResponse<Upload>>

    @GET("v1/member/report/type")
    fun getReportType(@Header("Authorization") auth: String): Call<RestApiResponse<ReportTypeList>>

    @POST("v1/member/report")
    fun setReport(@Header("Authorization") auth: String, @Body body: MemberReportBody): Call<RestApiResponse<Void>>

    @DELETE("v1/member/{bMid}/block")
    fun deleteBlock(
        @Header("Authorization") auth: String,
        @Path("bMid") blockMid: String,
    ): Call<RestApiResponse<BlockList>>

    @POST("v1/member/{bMid}/block")
    fun addBlock(
        @Header("Authorization") auth: String,
        @Path("bMid") blockMid: String,
    ): Call<RestApiResponse<BlockList>>

}