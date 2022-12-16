package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.service.NotificationService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException
import com.coinlive.chat.util.LoggerHelper

class NotificationRepository {
    private val service: NotificationService = RestApiClient.notificationService

    suspend fun setNotification(auth: String, coinId: String, notiType: String): Boolean {
        val response: RestApiResponse<Void>
        try {
            response = service.setNotification(auth, NotificationBody(coinId, notiType))
        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.setNotification error!")
        }
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

    suspend fun deleteNotification(auth: String, coinId: String, notiType: String): Boolean {
        val response: RestApiResponse<Void>

        try {
            response = service.deleteNotification(auth, NotificationBody(coinId, notiType))
        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.deleteNotification error!")
        }
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

    suspend fun getNotificationType(auth: String): List<NotificationType> {
        val response: RestApiResponse<NotificationTypeList>
        try {
            response = service.getNotificationType(auth)

        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.getNotificationType error!")
        }
        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!.typeList
    }

    suspend fun getNotificationSetting(auth: String, coinId: String): Map<String, Boolean> {
        val response: RestApiResponse<NotificationMap>

        try {
            response = service.getNotificationSetting(coinId, auth)
        } catch (exception: Exception) {
            throw NetworkException("NotificationRepository.getNotificationSetting error!")
        }

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(response.code, response.msg)
        }
        return response.d!!.notiMap
    }
}