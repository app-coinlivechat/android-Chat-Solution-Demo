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
    /v1/member/my/info [GET] {auth} // 해당 사용자의 회원 정보 조회
    /v1/member/report/type [GET] -> 준영님 확인 후 다시 노티
    /v1/member/report [POST] -> 준영님 확인 후 다시 노티
     */

    @GET("v1/member/check/nickname")
    fun isAvailableNickName(@Body nickName: NickNameBody): Call<RestApiResponse<NickName>>

    @PUT("v1/member/nickname")
    fun setNickName(
        @Header("Authorization") auth: String,
        @Body nickName: NickNameBody
    ): Call<RestApiResponse<NickNameBody>>

    @POST("v1/member/check")
    fun signupCheck(@Body firebaseUuid: MemberSignupCheckBody): Call<RestApiResponse<MemberSignupCheck>>

    @GET("v1/member/{mid}/info")
    fun getUserInfo(@Header("Authorization") auth: String, @Path("mid") id: String): Call<RestApiResponse<User>>

    @PUT("vv1/member/profile/basic")
    fun setBasicProfile(@Header("Authorization") auth: String): Call<RestApiResponse<Upload>>

    @GET("v1/member/my/info")
    fun getMyInfo(@Header("Authorization") auth: String): Call<RestApiResponse<MyInfo>>
}