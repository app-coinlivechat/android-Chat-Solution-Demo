package com.coinlive.chat.firebase

import android.content.Context
import androidx.room.Room
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.exception.SendMessageException
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.Emoji
import com.coinlive.chat.firebase.model.EmojiType
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.firebase.service.*
import com.coinlive.chat.util.CalendarHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface MessageListener {
    fun deletedMessage(chat: Chat)
    fun modifyMessage(chat: Chat)
    fun oldMessages(chatList: ArrayList<Chat>, isReload: Boolean)
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
    amaListener: AmaListener,
    applicationContext: Context,
) {

    companion object {
        /**
         * 테스트 환경을 위해 현재 build mode를 설정합니다.
         * 디폴트 값은 true 입니다.
         */
        var isDebug: Boolean = true
//            get() = field
//            set(value) {
//                field = value
//            }
    }

    private val realtimeDatabaseWrapper: RealtimeDatabaseWrapper =
        RealtimeDatabaseWrapper(coinId, cmNoticeListener, amaListener)
    private val firestoreWrapper: FirestoreWrapper = FirestoreWrapper(coinId, listener)
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
        deleteOldFailMessage()
    }


    /**
     * 채팅데이터 로드
     * 최근 메세지 리스트를 [standardSize] 만큼 로드한다.
     * 로드한 메세지는 [MessageListener.oldMessages]로 전달된다.
     * @param[standardSize] 한번에 로드할 리스트 사이즈
     * @param[notificationMap] 사용자의 Notification Map 데이터
     */
    fun fetchMessage(standardSize: Long = 50, notificationMap: Map<String, Boolean>) {
        if (isLoading) return else isLoading = true
        firestoreWrapper.fetchMessage(standardSize, notificationMap)
        // 끝나면
        isLoading = false
    }

    /**
     * 상대방 메세지의 이모지 리액션을 추가합니다.
     * @param[chat] 이모지 리액션을 추가할 메세지
     * @param[memberId] 자신의 id
     * @param[emojiType] 추가할 EmojiType
     */
    fun addEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {
        if (chat.memberId == memberId) return   // 자신의 메세지는 이모지를 추가 할 수 없다

        if (chat.emoji == null) {    // 메세지에 이모지가 아예 추가되어 있지 않을 경우
            firestoreWrapper.updateEmoji(chat.copy(emoji = hashMapOf(emojiType.key to Emoji(1, arrayListOf(memberId)))))
        } else {    // 메세지에 이모지가 있는 경우
            var emoji: Emoji? = chat.emoji[emojiType.key]
            if (emoji == null) { // emojiType이 추가되어 있지 않을 경우
                emoji = Emoji(1, arrayListOf(memberId))
            } else if (!emoji.mIds.contains(memberId)) { // emojiType이 추가되어 있지만 memberId가 추가 안되어 있을 경우
                val mIdList = emoji.mIds
                mIdList.add(memberId)
                emoji = Emoji(count = emoji.count + 1, mIds = mIdList)
            }

            if (chat.emoji[emojiType.key] != emoji) {  // 이전 chat.emoji 모델과 데이터가 다를 경우
                val copyEmoji : HashMap<String,Emoji> = HashMap()
                copyEmoji.putAll(chat.emoji)
                copyEmoji[emojiType.key] = emoji
                firestoreWrapper.updateEmoji(chat.copy(emoji = copyEmoji))
            }
        }
    }

    /**
     * 상대방 메세지의 추가한 이모지 리액션을 삭제합니다.
     * @param[chat] 이모지 리액션을 삭제할 메세지
     * @param[memberId] 자신의 id
     * @param[emojiType] 삭제할 EmojiType
     */
    fun removeEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {
        if (chat.memberId == memberId || chat.emoji == null) return   // 자신의 메세지와 chat.emoji에 데이터가 없는 경우는 이모지를 삭제 할 수 없다

        var emoji: Emoji? = chat.emoji[emojiType.key]
        if (emoji != null && emoji.mIds.contains(memberId)) { // emojiType의 이모지가 존재하고 emoji.mIds에 memberId가 존재할 경우
            val mIdList =  emoji.mIds
            mIdList.remove(memberId)
            emoji = Emoji(emoji.count - 1,mIdList)
        }

        if (emoji != null && chat.emoji[emojiType.key] != emoji) {  // 이전 chat.emoji 모델과 데이터가 다를 경우
            val copyEmoji : HashMap<String,Emoji> = HashMap()
            copyEmoji.putAll(chat.emoji)
            copyEmoji[emojiType.key] = emoji
            firestoreWrapper.updateEmoji(chat.copy(emoji = copyEmoji))
        }
    }

    /**
     * 채팅 메세지 발신 요청.
     * AMA가 진행 중이거나 [message]가 500자 이상일 경우 [SendMessageException]가 발생합니다.
     * 발신을 실패할 경우 [MessageListener.failSendMessage]로 해당 메세지([Chat])를 전달합니다.
     * @param[message] 메세지
     * @param[myInfo] 사용자 자신의 정보
     */
    fun sendMessage(message: String, myInfo: CustomerUser) {
        sendMessage(message, myInfo, null)
    }

    /**
     * 이미지 메세지를 전송합니다.
     * AMA가 진행 중이거나 [urlList]의 사이즈가 10개 이상일 경우 [SendMessageException]가 발생합니다.
     * 발신을 실패할 경우 [MessageListener.failSendMessage]로 해당 메세지([Chat])를 전달합니다.
     * @param[urlList] 이미지 url List
     * @param[myInfo] 사용자 자신의 정보
     */
    fun sendImage(urlList: ArrayList<String>, myInfo: CustomerUser) {
        sendMessage("", myInfo, urlList)
    }

    private fun sendMessage(message: String, myInfo: CustomerUser, urlList: ArrayList<String>?) {
        if (urlList != null && urlList.size > 10) {
            throw SendMessageException("이미지 10개 이상 보낼수 없습니다.")
        }

        if(urlList == null && message.trim().isEmpty()) {
            throw SendMessageException("message is Empty")
        }

        if (checkAmaProceeding()) { // ama 진행 체크
            throw SendMessageException("ama 상태에서는 메세지를 보낼수 없습니다.")
        }
        if (message.length > 500) {
            throw SendMessageException("메세지는 500자 보다 적어야 합니다.")
        }


        firestoreWrapper.sendMessage(createChat(message, myInfo, urlList), object : SendEventListener {
            override fun fail(chat: Chat) {
                chatDao.insertMessage(chat)
            }

            override fun success(chat: Chat) {}
        })
    }

    private fun checkAmaProceeding(): Boolean {
        val amaEndTimeStamp: Long = realtimeDatabaseWrapper.ama?.endTime ?: 0
        return amaEndTimeStamp > Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
    }

    private fun createChat(message: String = "", myInfo: CustomerUser, urlList: ArrayList<String>?): Chat {
        return Chat(
            koMessage = message, enMessage = message, firebaseAuthId = myInfo.firebaseUuid,
            st = firestoreWrapper.getServerTimeStamp(), exchange = customerName, symbol = coinSymbol, coinId = coinId,
            insertTime = CalendarHelper.nowCalendar().timeInMillis, messageType = MessageType.USER.toLowName(),
            chattingLocale = "ko", memberId = myInfo.id, images = urlList
        )
    }

    /**
     * 전송 실패한 메세지를 다시 전송 시도합니다.
     * 재 전송 메세지가 로컬 디비에 없는 경우 [SendMessageException]이 발생합니다.
     * 또한 [Chat.images]의 사이즈가 10개 이상이거나 재 전송 요청시 AMA가 진행 중이거나, [Chat.koMessage] 또는 [Chat.enMessage] 가 500자 이상일 경우
     * [SendMessageException]가 발생합니다.
     * 전송 실패할 경우 [MessageListener.failSendMessage] 로 해당 [Chat] 객체가 전달됩니다.
     * 전송 성공시 [MessageListener.retrySendMessageSuccess]로 성공한 [messageId]가 전달되며 로컬 DB에서 삭제됩니다.
     * @param[messageId] 재 전송할 메세지 id
     */
    fun retrySendMessage(messageId: String) {

        val chat = chatDao.getMessage(messageId) ?: throw SendMessageException("$messageId 는 실패 메세지 DB에 존재하지 않습니다.")


        if (chat.images != null && chat.images.size > 10) {
            throw SendMessageException("이미지 10개 이상 보낼수 없습니다.")
        }
        if(chat.images == null && (chat.koMessage.trim().isEmpty() || chat.enMessage.trim().isEmpty())) {
            throw SendMessageException("message is Empty")
        }

        if (checkAmaProceeding()) { // ama 진행 체크
            throw SendMessageException("ama 상태에서는 메세지를 보낼수 없습니다.")
        }
        if (chat.koMessage.length > 500 || chat.enMessage.length > 500) {
            throw SendMessageException("메세지는 500자 보다 적어야 합니다.")
        }
        val cloneChat = chat.copy(insertTime = CalendarHelper.nowCalendar().timeInMillis)

        firestoreWrapper.sendMessage(cloneChat, object : SendEventListener {
            override fun fail(chat: Chat) {
                // 재 전송 실패일 경우 아무것도 안함
            }

            override fun success(chat: Chat) {
                //  재전송 성공시 DB에서 삭제하기
                chatDao.deleteMessage(chat.messageId)
            }
        },isRetry = true)
    }

    /**
     * 로컬 디비에서 전송 실패 메세지를 삭제합니다.
     * @param[messageId] 삭제할 전송 실패 메세지 id
     */
    fun deleteFailMessage(messageId: String) {
        chatDao.deleteMessage(messageId)
    }

    /**
     * 로컬 디비에서 오늘 자정 기준으로 부터 7일 이전 전송 실패 데이터를 삭제합니다.
     */
    private fun deleteOldFailMessage() {
        chatDao.deleteOldMessage(CalendarHelper.getTodayMidnightTimeStamp())

    }

    /**
     * 메세지를 삭제합니다.
     * @param[chat] 삭제할 [Chat] object
     */
    fun deletedMessage(chat: Chat) {
        firestoreWrapper.deletedMessage(chat)
    }


    /**
     * 기존에 로드한 데이터를 재 필터링하여 로드합니다.
     * @param[notificationMap] 사용자 notification Map 데이터
     */
    fun reload(notificationMap: Map<String, Boolean>) {
        firestoreWrapper.reload(notificationMap)
    }

    /**
     * [CoinliveChat]을 더이상 사용하지 않을 경우 호출합니다.
     */
    fun dispose() {
        realtimeDatabaseWrapper.close()
        firestoreWrapper.close()
    }

}
