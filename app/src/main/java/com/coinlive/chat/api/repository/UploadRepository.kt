package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.Upload
import com.coinlive.chat.api.service.UploadService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class UploadRepository {
    private val service: UploadService = RestApiClient.uploadService

    fun uploadImage(auth: String, file: List<Int>): String {
        val response = service.uploadImage(auth, file).execute().body()
            ?: throw NetworkException("UploadRepository.uploadImage error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "UploadRepository.uploadImage fail. please check auth or file ",
                response.code, response.msg
            )
        }
        return response.d!!.url
    }

    fun deleteImage(auth: String, url: String): String {
        val response = service.deleteImage(auth, Upload(url)).execute().body()
            ?: throw NetworkException("UploadRepository.deleteImage error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "UploadRepository.deleteImage fail. please check auth or url ",
                response.code, response.msg
            )
        }
        return response.d!!.url
    }

    fun uploadProfile(auth: String, file: List<Int>, isNft: Boolean): String {
        val response = service.uploadProfile(auth, isNft, file).execute().body()
            ?: throw NetworkException("UploadRepository.uploadProfile error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "UploadRepository.uploadProfile fail. please check auth or file",
                response.code, response.msg
            )
        }
        return response.d!!.url
    }


}