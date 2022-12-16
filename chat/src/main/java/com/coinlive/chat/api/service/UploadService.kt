package com.coinlive.chat.api.service

import com.coinlive.chat.api.model.Upload
import com.coinlive.chat.api.model.RestApiResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface UploadService {
    @Multipart
    @POST("v1/upload/profile")
    suspend fun uploadProfile(
        @Header("Authorization") auth: String,
        @Query("nft") isNft: Boolean,
        @Part file: MultipartBody.Part,
    ): RestApiResponse<Upload>
}