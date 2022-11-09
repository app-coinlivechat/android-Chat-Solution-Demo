package com.coinlive.uikit.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.Coin
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.util.LoggerHelper
import kotlinx.coroutines.launch

class ChattingViewModel() : ViewModel() {
    private val TAG = ChattingViewModel::class.java.simpleName
    private var coinliveChat: CoinliveChat? = null
    private val coinliveApi = CoinliveRestApi()
    var channel:Channel? = null
        private set(value) {
            field = value
        }

    var myInfo:CustomerUser? = null
        private set(value) {
            field = value
        }

    fun initCoinLiveChat(
        myInfo:CustomerUser,
        channel:Channel,
        customerName: String,
        listener: MessageListener,
        cmNoticeListener: CmNoticeListener,
        amaListener: AmaListener,
        context: Context
    ) {
        this.myInfo = myInfo
        this.channel = channel
        coinliveChat = CoinliveChat(channel.coinId, channel.coinSymbol, customerName, listener, cmNoticeListener, amaListener,
            context)
    }

    fun fetchMessage() = viewModelScope.launch {
        if (channel == null) {
            LoggerHelper.de("channel is null")
            return@launch
        }
        coinliveApi.getNotificationSetting(coinId = channel!!.coinId, callback = object : ResponseCallback<Map<String,
                Boolean>> {
            override fun onSuccess(value: Map<String,Boolean>) {
                coinliveChat!!.fetchMessage(notificationMap = value)
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("${exception.message}")
            }

        })
    }
}