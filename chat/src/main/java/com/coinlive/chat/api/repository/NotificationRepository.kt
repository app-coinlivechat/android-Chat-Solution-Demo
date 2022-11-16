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

    suspend fun setNotification(auth: String, coinId: String, notiType: String): Boolean {
        try {
            val response = service.setNotification(auth, NotificationBody(coinId, notiType))
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
        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.setNotification error!")
        }
    }

    suspend fun deleteNotification(auth: String, coinId: String, notiType: String): Boolean {
        try {
            val response = service.deleteNotification(auth, NotificationBody(coinId, notiType))

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
        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.deleteNotification error!")
        }
    }

    suspend fun getNotificationType(auth: String): List<NotificationType> {
        try {
            val response = service.getNotificationType(auth)
            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "NotificationRepository.getNotificationType fail. please check auth or coinId ",
                    response.code, response.msg
                )
            }
            return response.d!!.typeList
        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.getNotificationType error!")
        }
    }

    suspend fun getNotificationSetting(auth: String, coinId: String): Map<String, Boolean> {
        try {
            val response = service.getNotificationSetting(coinId, auth)

            if (!response.isSuccess() && response.d == null) {
                throw RequestFailException(
                    "NotificationRepository.getNotificationSetting fail. please check auth or coinId ",
                    response.code, response.msg
                )
            }
            return response.d!!.notiMap
        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.getNotificationSetting error!")
        }
    }
}