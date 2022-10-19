package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.Upload
import com.coinlive.chat.api.model.RestApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadService {
    /*
    /v1/upload/image [POST] // 이미지 업로드, [DELET] // 이미지 삭제 - 채팅에서
    /v1/upload/profile [POST] // 유저 프로필 이미지 업로드
     */

    @Multipart
    @POST("v1/upload/image")
    fun uploadImage(@Header("Authorization") auth:String, @Part file:List<Int>) : Call<RestApiResponse<Upload>>

    @DELETE("v1/upload/image")
    fun deleteImage(@Header("Authorization") auth:String, @Body url:Upload) : Call<RestApiResponse<Upload>>

    @Multipart
    @POST("v1/upload/profile")
    fun uploadProfile(@Header("Authorization") auth:String, @Query("nft") isNft:Boolean, @Part file:List<Int>) :
            Call<RestApiResponse<Upload>>
}