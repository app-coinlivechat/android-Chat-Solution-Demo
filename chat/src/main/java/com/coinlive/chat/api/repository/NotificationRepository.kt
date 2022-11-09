package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.NotificationBody
import com.coinlive.chat.api.model.NotificationType
import com.coinlive.chat.api.service.NotificationService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException
import com.coinlive.chat.util.LoggerHelper

class NotificationRepository {
    private val service: NotificationService = RestApiClient.notificationService

    fun setNotification(auth: String, coinId: String, notiType: String): Boolean {
        val response = service.setNotification(auth, NotificationBody(coinId, notiType)).execute().body()
            ?: throw NetworkException("NotificationRepository.setNotification error!")

        return when {
            response.isSuccess() -> {
                true
            }
            else -> {
                LoggerHelper.de(
                    "NotificationRepository.setNotification fail. please check auth or coinId or " +
                            "notiType ${response.code}, ${response.msg}")
                false
            }
        }
    }

    fun deleteNotification(auth: String, coinId: String, notiType: String): Boolean {
        val response = service.deleteNotification(auth, NotificationBody(coinId, notiType)).execute().body()
            ?: throw NetworkException("NotificationRepository.deleteNotification error!")

        return when {
            response.isSuccess() -> {
                true
            }
            else -> {
                LoggerHelper.de(
                    "NotificationRepository.deleteNotification fail. please check auth or coinId or " +
                            "notiType ${response.code}, ${response.msg}")
                false
            }
        }
    }

    fun getNotificationType(auth: String, coinId: String): List<NotificationType> {
        val response = service.getNotificationType(coinId, auth).execute().body()
            ?: throw NetworkException("NotificationRepository.getNotificationType error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "NotificationRepository.getNotificationType fail. please check auth or coinId ",
                response.code, response.msg
            )
        }
        return response.d!!.typeList
    }

    suspend fun getNotificationSetting(auth: String, coinId: String): Map<String,Boolean> {
        try{
            val response = service.getNotificationSetting(coinId, auth)

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "NotificationRepository.getNotificationSetting fail. please check auth or coinId ",
                    response.code, response.msg
                )
            }
            return response.d!!.notiMap
        }catch (exception : Exception) {
            throw NetworkException("NotificationRepository.getNotificationSetting error!")
        }
    }
}