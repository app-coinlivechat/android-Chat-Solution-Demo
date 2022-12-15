package com.coinlive.uikit.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.DynamicLinkListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.EmojiType
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.models.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatViewModel : ViewModel() {
    private val TAG = ChatViewModel::class.java.simpleName
    private var coinliveChat: CoinliveChat? = null
    private val coinliveApi = CoinliveRestApi()
    val userCount: MutableLiveData<Int> = MutableLiveData(0)
    val userStatus: MutableLiveData<UserStatus> = MutableLiveData(UserStatus.NONE)
    val cm: MutableLiveData<String> = MutableLiveData<String>(null)
    val originNotiList = ArrayList<Notification>()
    val reportType = ArrayList<ReportType>()
    var standardSize = 50
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
        standardSize: Int?,
        channel: Channel,
        customerName: String,
        listener: MessageListener,
        cmNoticeListener: CmNoticeListener,
        amaListener: AmaListener,
        context: Context,
    ) {
        this.channel = channel
        myInfo?.let {
            this.myInfo = it
            this.userStatus.value = it.status
        }

        standardSize?.let {
            this.standardSize = standardSize
        }
        coinliveChat =
            CoinliveChat(channel.coinId, channel.coinSymbol, customerName, listener, cmNoticeListener, amaListener,
                context)
        if(myInfo != null) {
            loadReportType()
            loadNotificationType()
        } else {
            fetchMessage()
        }
        startTimer()
    }

    fun fetchMessage() = viewModelScope.launch {
        if (channel == null) {
            LoggerHelper.de("channel is null")
            return@launch
        }
        coinliveChat!!.fetchMessage(standardSize = standardSize.toLong(), notificationMap = getNotificationMap())
    }

    private fun loadNotification(list: List<NotificationType>) = viewModelScope.launch {
        if (channel == null) return@launch

        coinliveApi.getNotificationSetting(channel!!.coinId, callback = object : ResponseCallback<Map<String,
                Boolean>> {
            override fun onSuccess(value: Map<String, Boolean>) {
                val result = ArrayList<Notification>()
                list.forEach { type ->
                    val enable: Boolean? = value[type.type]
                    if(enable != null && type.tag == "CHAT") {
                        result.add(Notification(type.type, type.name, enable))
                    }
                }
                if (result.isNotEmpty()) {
                    originNotiList.addAll(result)
                }
                fetchMessage()
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("${exception.message}")
            }
        })
    }

    private fun loadNotificationType() = viewModelScope.launch {
        coinliveApi.getNotificationType(callback = object : ResponseCallback<List<NotificationType>> {
            override fun onSuccess(value: List<NotificationType>) {
                loadNotification(value)
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("${exception.message}")
            }
        })
    }

    private fun startTimer() {
        if (channel == null || myInfo == null) return


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

    private fun getNotificationMap(): Map<String, Boolean> {
        val map = HashMap<String, Boolean>()

        originNotiList.forEach {
            map[it.id] = it.enable
        }

        return map
    }

    fun setNotification(list: ArrayList<Notification>) = viewModelScope.launch {
        if (channel == null) return@launch
        list.forEach { noti ->
            val origin: Notification? = originNotiList.find { it.id == noti.id }
            origin?.let {
                if (origin.enable != noti.enable) {
                    Log.d(TAG, "new : $noti, origin : $origin")

                    if (noti.enable) {
                        setNotification(noti.id)
                    } else {
                        deleteNotification(noti.id)
                    }
                }
            }
        }
        originNotiList.clear()
        originNotiList.addAll(list)
    }

    private fun deleteNotification(type: String) = viewModelScope.launch {
        coinliveApi.deleteNotification(this@ChatViewModel.channel!!.coinId, notiType = type, object :
            ResponseCallback<Boolean> {
            override fun onSuccess(value: Boolean) {
//                LoggerHelper.di("$type Notification change success")
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("$type deleteNotification fail\n ${exception.message}")
            }

        })
    }


    private fun setNotification(type: String) = viewModelScope.launch {
        coinliveApi.setNotification(this@ChatViewModel.channel!!.coinId, notiType = type, object :
            ResponseCallback<Boolean> {
            override fun onSuccess(value: Boolean) {
//                LoggerHelper.di("$type Notification change success")
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de("$type setNotification fail\n ${exception.message}")
            }

        })
    }

    private fun loadReportType() = viewModelScope.launch {
        coinliveApi.getReportType(object : ResponseCallback<List<ReportType>> {
            override fun onSuccess(value: List<ReportType>) {
                this@ChatViewModel.reportType.addAll(value)
            }

            override fun onFail(exception: CoinliveException) {
                LoggerHelper.de(exception.stackTraceToString())
            }

        })
    }

    fun sendMessage(text: String) {
        if (myInfo == null) {
            return
        }

        coinliveChat?.sendMessage(text, myInfo!!)
    }


    fun retryFailMessage(item: Chat) {
        coinliveChat?.retrySendMessage(item.messageId)
    }


    fun addEmoji(chat: Chat, key: String) {
        if (myInfo == null) return
        coinliveChat?.addEmoji(chat, myInfo!!.id, getEmojiType(key))
    }

    fun deleteEmoji(chat: Chat, key: String) {
        if (myInfo == null) return
        coinliveChat?.deleteEmoji(chat, myInfo!!.id, getEmojiType(key))
    }

    fun deleteMessage(chat: Chat) {
        if (chat.insertTime < 1) {
            CoroutineScope(Dispatchers.IO).launch {
                coinliveChat?.deleteFailMessage(chat)
            }
        } else {
            coinliveChat?.deletedMessage(chat)
        }
    }


    fun addBlock(mId: String, callback: ResponseCallback<ArrayList<String>>) = viewModelScope.launch {
        coinliveApi.addBlock(mId, object : ResponseCallback<ArrayList<String>> {
            override fun onSuccess(value: ArrayList<String>) {
                myInfo = myInfo!!.copy(blockUserMidList = value)
                callback.onSuccess(value)
            }

            override fun onFail(exception: CoinliveException) {
                callback.onFail(exception)

            }
        })
    }

    fun deleteBlock(mId: String, callback: ResponseCallback<ArrayList<String>>) = viewModelScope.launch {
        coinliveApi.deleteBlock(mId, object : ResponseCallback<ArrayList<String>> {
            override fun onSuccess(value: ArrayList<String>) {
                myInfo = myInfo!!.copy(blockUserMidList = value)
                callback.onSuccess(value)
            }

            override fun onFail(exception: CoinliveException) {
                callback.onFail(exception)
            }
        })
    }

    fun isBlockUser(mId: String): Boolean {
        return myInfo != null && myInfo!!.blockUserMidList.contains(mId)
    }

    fun report(reportType: ReportType, mId: String, callback: ResponseCallback<Boolean>) = viewModelScope.launch {
        coinliveApi.setReport(reportMid = mId, reportTypeId = reportType.typeId, callback)
    }

    fun getOldFailMessage(): ArrayList<Chat>? {
        return coinliveChat?.getFailedMessages()
    }

    fun getDynamicLink(listener: DynamicLinkListener) {
        coinliveChat?.getDynamicLink(listener)
    }


    private fun getEmojiType(key: String): EmojiType {
        return when (key) {
            EmojiType.CLAP.key -> EmojiType.CLAP
            EmojiType.GOOD.key -> EmojiType.GOOD
            EmojiType.HEART.key -> EmojiType.HEART
            EmojiType.ROCKET.key -> EmojiType.ROCKET
            EmojiType.CRY.key -> EmojiType.CRY
            else -> EmojiType.ASTONISHED
        }
    }


    fun destroy() {
        timer?.cancel()
        timer = null
        coinliveChat?.close()
    }
}