package com.coinlive.uikit.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.api.model.UserCount
import com.coinlive.chat.api.model.UserStatus
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.util.LoggerHelper
import kotlinx.coroutines.launch
import java.util.*

class ChattingViewModel : ViewModel() {
    private val TAG = ChattingViewModel::class.java.simpleName
    private var coinliveChat: CoinliveChat? = null
    private val coinliveApi = CoinliveRestApi()
    val userCount : MutableLiveData<Int> = MutableLiveData()
    val userStatus : MutableLiveData<UserStatus> = MutableLiveData()
    var timer : Timer? = null

    var channel: Channel? = null
        private set(value) {
            field = value
        }

    var myInfo: CustomerUser? = null
        private set(value) {
            field = value
        }

    fun initCoinLiveChat(
        myInfo: CustomerUser,
        channel: Channel,
        customerName: String,
        listener: MessageListener,
        cmNoticeListener: CmNoticeListener,
        amaListener: AmaListener,
        context: Context,
    ) {
        this.myInfo = myInfo
        this.channel = channel
        coinliveChat =
            CoinliveChat(channel.coinId, channel.coinSymbol, customerName, listener, cmNoticeListener, amaListener,
                context)
    }

    fun fetchMessage() = viewModelScope.launch {
        if (channel == null) {
            LoggerHelper.de("channel is null")
            return@launch
        }
        coinliveApi.getNotificationSetting(coinId = channel!!.coinId, callback = object : ResponseCallback<Map<String,
                Boolean>> {
            override fun onSuccess(value: Map<String, Boolean>) {
                startTimer()
                coinliveChat!!.fetchMessage(notificationMap = value)
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("${exception.message}")
            }

        })
    }

    private fun startTimer() {
        if(channel == null) return

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                getUserCount()
            }
        }, 0, 1000 * 60)
    }

    private fun getUserCount() = viewModelScope.launch{
        coinliveApi.getUserCount(channel!!.coinId,object : ResponseCallback<UserCount> {
            override fun onSuccess(value: UserCount) {
                userCount.value = value.count
                value.status?.let {
                    userStatus.value = it
                }

            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("${exception.message}")
            }

        })
    }

    fun destroy() {
        timer?.cancel()
        timer = null
        coinliveChat?.close()
    }
}