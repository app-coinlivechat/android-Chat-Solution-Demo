package com.coinlive.chat.api.repository

import com.coinlive.chat.api.RestApiClient
import com.coinlive.chat.api.model.NotificationBody
import com.coinlive.chat.api.model.NotificationType
import com.coinlive.chat.api.service.NotificationService
import com.coinlive.chat.exception.NetworkException
import com.coinlive.chat.exception.RequestFailException

class NotificationRepository {
    private val service: NotificationService = RestApiClient.notificationService

    fun setNotification(auth: String, coinId: String, notiType: String): Boolean {
        val response = service.setNotification(auth, NotificationBody(coinId, notiType)).execute().body()
            ?: throw NetworkException("NotificationRepository.setNotification error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "NotificationRepository.setNotification fail. please check auth or coinId or " +
                        "notiType",
                response.code, response.msg
            )
        }
        return true
    }

    fun deleteNotification(auth: String, coinId: String, notiType: String): Boolean {
        val response = service.deleteNotification(auth, NotificationBody(coinId, notiType)).execute().body()
            ?: throw NetworkException("NotificationRepository.deleteNotification error!")

        if (!response.isSuccess() && response.d == null) {
            throw RequestFailException(
                "NotificationRepository.deleteNotification fail. please check auth or coinId or " +
                        "notiType",
                response.code, response.msg
            )
        }
        return true
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
}