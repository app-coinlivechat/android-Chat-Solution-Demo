package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.Upload
import com.coinlive.chat.api.service.UploadService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException
import okhttp3.MultipartBody

class UploadRepository {
    private val service: UploadService = RestApiClient.uploadService

    suspend fun uploadImage(auth: String, image: MultipartBody.Part): String {
        try{
            val response = service.uploadImage(auth, image)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "UploadRepository.uploadImage fail. please check auth or file ",
                    response.code, response.msg
                )
            }
            return response.d!!.url
        } catch (exception:Exception) {
            exception.printStackTrace()

            throw NetworkException("UploadRepository.uploadImage error!")
        }
    }

    suspend fun deleteImage(auth: String, url: String): String {
        try{
            val response = service.deleteImage(auth, Upload(url))
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "UploadRepository.deleteImage fail. please check auth or url ",
                    response.code, response.msg
                )
            }
            return response.d!!.url
        }catch (exception:Exception) {
            throw NetworkException("UploadRepository.deleteImage error!")
        }
    }

    suspend fun uploadProfileImage(auth: String, image: MultipartBody.Part): String {
        try{
            val response = service.uploadProfile(auth, false, image)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "UploadRepository.uploadProfile fail. please check auth or file",
                    response.code, response.msg
                )
            }
            return response.d!!.url
        }catch (exception:Exception) {
            throw NetworkException("UploadRepository.uploadProfile error!")
        }
    }

}