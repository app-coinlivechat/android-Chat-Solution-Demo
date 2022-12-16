package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.RestApiResponse
import com.coinlive.chat.api.model.Upload
import com.coinlive.chat.api.service.UploadService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException
import okhttp3.MultipartBody

class UploadRepository {
    private val service: UploadService = RestApiClient.uploadService

    suspend fun uploadProfileImage(auth: String, image: MultipartBody.Part): String {
        val response: RestApiResponse<Upload>
        try {
            response = service.uploadProfile(auth, false, image)
        } catch (exception: Exception) {
            throw NetworkException("UploadRepository.uploadProfile error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!.url
    }

}