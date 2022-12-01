package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.Upload
import com.coinlive.chat.api.model.RestApiResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface UploadService {
    /*
    /v1/upload/image [POST] // 이미지 업로드, [DELET] // 이미지 삭제 - 채팅에서
    /v1/upload/profile [POST] // 유저 프로필 이미지 업로드
     */

    @Multipart
    @POST("v1/upload/image")
    suspend fun uploadImage(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
    ): RestApiResponse<Upload>

    @HTTP(method = "DELETE", path = "v1/upload/image", hasBody = true)
    suspend fun deleteImage(@Header("Authorization") auth: String, @Body url: Upload): RestApiResponse<Upload>

    @Multipart
    @POST("v1/upload/profile")
    suspend fun uploadProfile(
        @Header("Authorization") auth: String,
        @Query("nft") isNft: Boolean,
        @Part file: MultipartBody.Part,
    ): RestApiResponse<Upload>
}