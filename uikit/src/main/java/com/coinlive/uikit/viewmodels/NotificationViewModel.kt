package com.coinlive.uikit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.*
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.models.Notification
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface LoadNotificationListListener{
    fun success(list : ArrayList<Notification>)
    fun fail(exception: Exception)
}


class NotificationViewModel : ViewModel() {
    private val TAG = NotificationViewModel::class.java.simpleName
    private val coinliveApi = CoinliveRestApi()
    private var listener : LoadNotificationListListener? = null
    val originNotiMap = HashMap<String, Boolean>()
    var coinId: String? = null

    private fun loadNotification(list: List<NotificationType>) = viewModelScope.launch {
        if (coinId == null) return@launch

        coinliveApi.getNotificationSetting(coinId!!, callback = object : ResponseCallback<Map<String,
                Boolean>> {
            override fun onSuccess(value: Map<String, Boolean>) {
                originNotiMap.putAll(value)
                val result = ArrayList<Notification>()
                list.forEach {
                    val enable: Boolean? = value[it.type]
                    enable?.let { enable ->
                        result.add(Notification(it.type, it.name, enable))
                    }
                }
                if (result.isNotEmpty()) {
                    listener?.success(result)
                }

            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("${exception.message}")
            }
        })
    }

    fun loadNotificationType(coinId: String, listener : LoadNotificationListListener) = viewModelScope.launch {
        this@NotificationViewModel.coinId = coinId
        this@NotificationViewModel.listener = listener

        coinliveApi.getNotificationType(callback = object : ResponseCallback<List<NotificationType>> {
            override fun onSuccess(value: List<NotificationType>) {
                loadNotification(value)
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("${exception.message}")
            }
        })
    }

    fun setNotification(list: ArrayList<Notification>) = viewModelScope.launch {
        if (coinId == null) return@launch

        list.forEach { noti ->
            val enable: Boolean? = originNotiMap[noti.id]
            enable?.let {
                if (enable != noti.enable) {
                    if (noti.enable) {
                        setNotification(noti.id)
                    } else {
                        deleteNotification(noti.id)
                    }
                }
            }
        }

    }

    private fun deleteNotification(type: String) = viewModelScope.launch {
        coinliveApi.deleteNotification(this@NotificationViewModel.coinId!!, notiType = type, object :
            ResponseCallback<Boolean> {
            override fun onSuccess(value: Boolean) {
                LoggerHelper.di("$type Notification change success")
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.di("$type Notification change fail")
            }

        })
    }


    private fun setNotification(type: String) = viewModelScope.launch {
        coinliveApi.setNotification(this@NotificationViewModel.coinId!!, notiType = type, object :
            ResponseCallback<Boolean> {
            override fun onSuccess(value: Boolean) {
                LoggerHelper.di("$type Notification change success")
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.di("$type Notification change fail")
            }

        })
    }


}