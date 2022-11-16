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
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.util.LoggerHelper
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel : ViewModel() {
    private val TAG = ChatViewModel::class.java.simpleName
    private var coinliveChat: CoinliveChat? = null
    private val coinliveApi = CoinliveRestApi()
    val userCount: MutableLiveData<Int> = MutableLiveData(0)
    val userStatus: MutableLiveData<UserStatus> = MutableLiveData(UserStatus.NONE)
    var timer: Timer? = null

    var channel: Channel? = null
        private set(value) {
            field = value
        }

    var myInfo: CustomerUser? = null
        private set(value) {
            field = value
        }

    fun initCoinLiveChat(
        myInfo: CustomerUser?,
        channel: Channel,
        customerName: String,
        listener: MessageListener,
        cmNoticeListener: CmNoticeListener,
        amaListener: AmaListener,
        context: Context,
    ) {
        this.channel = channel
        this.myInfo = myInfo
        //TODO notification 3. fetchMessage
        loadNotification()

        coinliveChat =
            CoinliveChat(channel.coinId, channel.coinSymbol, customerName, listener, cmNoticeListener, amaListener,
                context)
    }

    fun loadCustomerUser() = viewModelScope.launch {
        coinliveApi.getCustomerMemberInfo(object : ResponseCallback<CustomerUser> {
            override fun onSuccess(value: CustomerUser) {
                this@ChatViewModel.myInfo = value
                this@ChatViewModel.userStatus.value = value.status
            }

            override fun onFail(exception: CoinliveException) {
                this@ChatViewModel.userStatus.value = UserStatus.NONE
            }

        })
    }

    fun fetchMessage() = viewModelScope.launch {
        if (channel == null) {
            LoggerHelper.de("channel is null")
            return@launch
        }


    }

    private fun loadNotification() = viewModelScope.launch {
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
        if (channel == null) return

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                getUserCount()
            }
        }, 0, 1000 * 60)
    }

    private fun getUserCount() = viewModelScope.launch {
        coinliveApi.getUserCount(channel!!.coinId, object : ResponseCallback<UserCount> {
            override fun onSuccess(value: UserCount) {
                userCount.value = value.count
                value.status?.let {
                    userStatus.value = it
                } ?: run {
                    userStatus.value = UserStatus.NONE
                }

            }

            override fun onFail(exception: CoinliveException) {
                userStatus.value = UserStatus.NONE
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