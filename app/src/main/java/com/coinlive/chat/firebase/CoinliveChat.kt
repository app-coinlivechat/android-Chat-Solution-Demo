package com.coinlive.chat.firebase

import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.EmojiType
import com.coinlive.chat.firebase.service.CmNoticeListener
import com.coinlive.chat.firebase.service.Firestore
import com.coinlive.chat.firebase.service.RealTimeDatabase

interface MessageListener {
    fun removeMessage(chat: Chat)
    fun modifyMessage(chat: Chat)
    fun oldMessages(chatList: List<Chat>, isReload: Boolean)
    fun newMessages(chat: Chat)
    fun failSendMessage(chat: Chat)
    fun retrySendMessageSuccess(messageId: String)
}


class CoinliveChat(
    coinId: String,
    coinSymbol: String,
    customerName: String,
    listener: MessageListener,
    cmNoticeListener: CmNoticeListener
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
    private val firestore: Firestore = Firestore(coinId,listener)
    private var isLoading: Boolean = false

    init {

        //TODO DB connect
        // 7일 이후 실패 메세지 삭제 로직 필요
    }


    /**
     *
     */
    fun fetchMessage(standardSize: Long = 50, notificationMap: Map<String, Boolean>) {
        if (isLoading) return else isLoading = true
        firestore.fetchMessage(standardSize,notificationMap)
        // 끝나면
        isLoading = false
    }

    fun removeMessage(messageId: String) {}

    fun addEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {}

    fun removeEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {}

    fun sendMessage(message: String, myInfo: CustomerUser, firebaseUuid: String) {
        //TODO ama 상태 체크

    }

    fun retryMessage(chat:Chat){
        //TODO db 삭제 -> chat data 복사 & 현재 시간으로 변경
        // 여기서도 fail이면 failSendMessage 보내주기 & 성공시에는 retrySendMessageSuccess 보내주기
    }

    private fun addFailMessage(){}

    fun deleteFailMessage(messageId: String){

    }

    fun sendImage(urlList: List<String>, myInfo: CustomerUser, firebaseUuid: String) {}

    /**
     * 기존에 로드한 데이터를 다시 필터링 한다.
     */
    fun reload(notificationMap: Map<String, Boolean>) {

    }


}
