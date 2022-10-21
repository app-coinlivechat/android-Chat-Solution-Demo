package com.coinlive.chat.firebase

import android.content.Context
import androidx.room.Room
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.EmojiType
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.firebase.service.*
import com.coinlive.chat.util.CalendarHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface MessageListener {
    fun removeMessage(chat: Chat)
    fun modifyMessage(chat: Chat)
    fun oldMessages(chatList: List<Chat>, isReload: Boolean)
    fun newMessages(chat: Chat)
    fun failSendMessage(chat: Chat)
    fun retrySendMessageSuccess(messageId: String)
}


class CoinliveChat(
    private val coinId: String,
    private val coinSymbol: String,
    private val customerName: String,
    listener: MessageListener,
    cmNoticeListener: CmNoticeListener,
    applicationContext: Context
) {

    companion object {
        /**
         * 테스트 환경을 위해 현재 build mode를 설정합니다.
         * 디폴트 값은 true 입니다.
         */
        var isDebug: Boolean = true
            get() = field
            set(value) {
                field = value
            }
    }

    private val realTime: RealTimeDatabase = RealTimeDatabase(coinId, cmNoticeListener)
    private val firestore: Firestore = Firestore(coinId, listener)
    private var isLoading: Boolean = false
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ChatDatabase::class.java,
            "coinlive_chat"
        ).build()
    }
    private val chatDao by lazy {
        db.chatDao()
    }

    init {
        GlobalScope.launch {
            deleteOldFailMessage()
        }
    }


    /**
     *
     */
    fun fetchMessage(standardSize: Long = 50, notificationMap: Map<String, Boolean>) {
        if (isLoading) return else isLoading = true
        firestore.fetchMessage(standardSize, notificationMap)
        // 끝나면
        isLoading = false
    }


    fun addEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {}

    fun removeEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {}

    /**
     * 채팅 메세지 발신 요청.
     * Ama 가 진행 중일 경우 메세지를 보내지 못합니다.
     * 발신을 실패할 경우 [MessageListener.failSendMessage]로 해당 메세지([Chat])를 전달합니다.
     * @param[message] 메세지
     * @param[myInfo] 사용자 자신의 정보
     */
    fun sendMessage(message: String, myInfo: CustomerUser) {
        //TODO ama 진행 중이면 sendMessage 못하게 구현 필요
//        val amaEndTimeStamp : Long = if(realTime.ama == null || realTime.ama?.endTime? == null) 0 else realTime.ama?.endTime?
//        realTime.ama?.endTime?.let {
//            if(it > CalendarHelper.nowCalendar().timeInMillis) {
//
//            }
//        }

        firestore.sendMessage(createChat(message, myInfo), object : SendFailListener {
            override fun sendFail(chat: Chat) {
                chatDao.insertMessage(chat)
            }
        })
    }

    private fun createChat(message: String, myInfo: CustomerUser): Chat {
        return Chat(
            koMessage = message, enMessage = message, firebaseAuthId = myInfo.firebaseUuid,
            st = firestore.getServerTimeStamp(), exchange = customerName, symbol = coinSymbol, coinId = coinId,
            insertTime = CalendarHelper.nowCalendar().timeInMillis, messageType = MessageType.USER.toLowName(),
            chattingLocale = "ko", memberId = myInfo.id,
        )
    }

    /**
     * 전송 실패한 메세지를 다시 전송 시도합니다.
     * 전송 실패할 경우 [MessageListener.failSendMessage] 로 해당 [chat] 객체가 전달됩니다.
     * @param[chat] 재전송할 메세지
     *
     */
    fun retryMessage(chat: Chat) {
        //TODO db 삭제 -> chat data 복사 & 현재 시간으로 변경
        // 여기서도 fail이면 failSendMessage 보내주기 & 성공시에는 retrySendMessageSuccess 보내주기
    }

    private fun addFailMessage() {
//        chatDao.insertMessage()
    }

    /**
     * 로컬 디비에서 전송 실패 메세지를 삭제합니다.
     * @param[messageId] 삭제할 전송 실패 메세지 id
     */
    suspend fun deleteFailMessage(messageId: String) {
        chatDao.deleteMessage(messageId)
    }

    /**
     * 로컬 디비에서 오늘 자정 기준으로 부터 7일 이전 전송 실패 데이터를 삭제합니다.
     */
    private suspend fun deleteOldFailMessage() {
        chatDao.deleteOldMessage(CalendarHelper.getTodayMidnightTimeStamp())

    }

    fun sendImage(urlList: List<String>, myInfo: CustomerUser, firebaseUuid: String) {}

    /**
     * 기존에 로드한 데이터를 다시 필터링 한다.
     */
    fun reload(notificationMap: Map<String, Boolean>) {

    }




}
