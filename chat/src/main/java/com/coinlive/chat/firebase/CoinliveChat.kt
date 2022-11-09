package com.coinlive.chat.firebase

import android.content.Context
import androidx.room.Room
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.Customer
import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.exception.FetchMessageException
import com.coinlive.chat.exception.SendMessageException
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.Emoji
import com.coinlive.chat.firebase.model.enum.EmojiType
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.firebase.service.*
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Coinlive 채팅 솔루션을 사용하기 위한 class 입니다.
 *
 * 각 코인 채팅방의 화면 마다 CoinliveChat 이 생성되어야 합니다.
 *
 * 채널간 전환/변경 시 해당 채널에 맞는 object를 사용해주세요.
 *
 * 코인라이브는 일반적으로 하나의 채팅이 하나의 코인/토큰과 매칭되어 사용됩니다.
 *
 * Coinlive는 Firebase RealTimeDatabase 와 Cloud Firestore를 사용하기 때문에 반드시
 * [CoinliveAuthentication.signIn] 을 선행애햐 합니다.
 *
 * 마지막으로 채널간 전환/변경하거나 종료시 [close]를 호출해 object를 해제해주세요.
 * @param[coinId] 채널의 코인 아이디 ([CoinliveRestApi.getChannelList]를 통해 받아온 [Channel.coinId]를 이용하세요)
 * @param[coinSymbol] 채널의 코인 심볼 ([CoinliveRestApi.getChannelList]를 통해 받아온 [Channel.coinSymbol]을 이용하세요)
 * @param[customerName] 커스토머의 이름 ([CoinliveRestApi.getCustomerInfo]를 통해 받아온 [Customer.name]을 이용하세요)
 * @param[listener] 신규,삭제,수정,로드 등 메세지들의 이벤트 Listener
 * @param[cmNoticeListener] CM 공지 사항을 전달 받기 위한 Listener
 * @param[amaListener] AMA 상태를 전달 받기 위한 Listener
 * @param[context] 전송 실패 메세지를 로컬 DB에 저장하기 위한 context
 */
class CoinliveChat(
    private val coinId: String,
    private val coinSymbol: String,
    private val customerName: String,
    listener: MessageListener,
    cmNoticeListener: CmNoticeListener,
    amaListener: AmaListener,
    private val context: Context,
) {

    private val realtimeDatabaseWrapper: RealtimeDatabaseWrapper =
        RealtimeDatabaseWrapper(coinId, cmNoticeListener, amaListener)
    private val firestoreWrapper: FirestoreWrapper = FirestoreWrapper(coinId, listener)
    private val db by lazy {
        Room.databaseBuilder(
            context,
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
     * 해당 채널의 채팅을 가져오는 function입니다.
     *
     * 이 function을 호출하면 채팅내용이 [MessageListener.oldMessages]을 통해 전달됩니다. 이때 [isReload] 값은 false입니다.
     * @param[standardSize] 한번에 로드할 리스트 사이즈
     * @param[notificationMap] 사용자의 Notification Map 데이터, 해당 값은 [] api를 이용해서 받아온 [Coin.notificationMap]을 이용해서 전달하십시요.
     * @throws FetchMessageException
     */
    fun fetchMessage(standardSize: Long = 50, notificationMap: Map<String, Boolean>) {
        if (standardSize > 50) throw FetchMessageException("CoinliveChat.fetchMessage error")
        firestoreWrapper.fetchMessage(standardSize, notificationMap)
    }

    /**
     * 기존에 로드한 데이터를 재 필터링하여 로드합니다.
     *
     * 이 function을 호출하면 채팅내용이 [MessageListener.oldMessages]을 통해 전달됩니다. 이 때 [isReload]의 값은
     * true입니다.
     * @param[notificationMap] 사용자 notification Map 데이터
     */
    fun reloadMessages(notificationMap: Map<String, Boolean>) {
        firestoreWrapper.reload(notificationMap)
    }
    /**
     * 채팅 메세지를 전송합니다.
     *
     * AMA가 진행 중이거나 [message]가 500자 이상일 경우 [SendMessageException]가 발생합니다.
     *
     * 발신을 실패할 경우 [MessageListener.failSendMessage]로 해당 메세지([Chat])룰 전달합니다.
     * @param[message] 메세지
     * @param[myInfo] 사용자 자신의 정보
     * @throws SendMessageException
     */
    fun sendMessage(message: String, myInfo: CustomerUser) {
        sendMessage(message, myInfo, null)
    }

    /**
     * 이미지 메세지를 전송합니다.
     *
     * AMA가 진행 중이거나 [urlList]의 사이즈가 10개 이상일 경우 [SendMessageException]가 발생합니다.
     *
     * 발신을 실패할 경우 [MessageListener.failSendMessage]로 해당 메세지([Chat])를 전달합니다.
     * @param[urlList] 이미지 url List
     * @param[myInfo] 사용자 자신의 정보
     * @throws SendMessageException
     */
    fun sendImage(urlList: ArrayList<String>, myInfo: CustomerUser) {
        sendMessage("", myInfo, urlList)
    }

    /**
     * 전송 실패한 메세지를 다시 전송 시도합니다.
     *
     * 재 전송 메세지가 로컬 디비에 없는 경우 [SendMessageException]이 발생합니다.
     *
     * 또한 [Chat.images]의 사이즈가 10개 이상이거나 재 전송 요청시 AMA가 진행 중이거나, [Chat.koMessage] 또는 [Chat.enMessage] 가 500자 이상일 경우
     * [SendMessageException]가 발생합니다.
     *
     * 전송 실패할 경우 [MessageListener.failSendMessage] 로 해당 [Chat] 객체가 전달됩니다.
     *
     * 전송 성공시 [MessageListener.retrySendMessageSuccess]로 성공한 [messageId]가 전달되며 로컬 DB에서 삭제됩니다.
     * @param[messageId] 재 전송할 메세지 id
     * @throws SendMessageException
     */
    fun retrySendMessage(messageId: String) {
        val chat = chatDao.getMessage(messageId) ?: throw SendMessageException("$messageId 는 실패 메세지 DB에 존재하지 않습니다.")

        chat.images?.let {
            if (it.size > 10) {
                throw SendMessageException("이미지 10개 이상 보낼수 없습니다.")
            }
        } ?: run {
            if (chat.koMessage.trim().isEmpty() || chat.enMessage.trim().isEmpty()) {
                throw SendMessageException("메세지는 1자 보다 크고 500자 보다 적어야 합니다.")
            }
        }

        if (checkAmaProceeding()) { // ama 진행 체크
            throw SendMessageException("ama 상태에서는 메세지를 보낼수 없습니다.", isAma = true)
        }
        if (chat.koMessage.length > 500 || chat.enMessage.length > 500) {
            throw SendMessageException("메세지는 1자 보다 크고 500자 보다 적어야 합니다.")
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
        }, isRetry = true)
    }

    /**
     * 메세지를 삭제합니다.
     * @param[chat] 삭제할 [Chat] object
     */
    fun deletedMessage(chat: Chat) {
        firestoreWrapper.deletedMessage(chat)
    }

    /**
     * 로컬 디비에서 전송 실패 메세지를 삭제합니다.
     * @param[messageId] 삭제할 전송 실패 메세지 [Chat.messageId]
     */
    fun deleteFailMessage(messageId: String) {
        chatDao.deleteMessage(messageId)
    }

    /**
     * 상대방 메세지의 이모지 리액션을 추가합니다.
     * [chat.memberId]와 [memberId] 가 동일할 경우 자신의 메세지로 판단하여 이모지를 추가하지 않습니다.
     * 리액션이 성공적으로 추가될 경우 [MessageListener.modifyMessage]를 통해 전달됩니다.
     * @param[chat] 이모지 리액션을 추가할 메세지
     * @param[memberId] 자신의 id ([CustomerUser.id] 값을 이용하세요.)
     * @param[emojiType] 추가할 EmojiType
     */
    fun addEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {
        if (chat.memberId == memberId) {// 자신의 메세지는 이모지를 추가할 수 없다
            LoggerHelper.de("자신의 메세지는 이모지 리액션을 추가할 수 없습니다.")
            return
        }

        chat.emoji?.let {
            // 메세지에 이모지가 아예 추가되어 있지 않을 경우
            var emoji: Emoji? = it[emojiType.key]
            if (emoji == null) { // emojiType이 추가되어 있지 않을 경우
                emoji = Emoji(1, arrayListOf(memberId))
            } else if (emoji.mIds != null && !emoji.mIds!!.contains(memberId)) { // emojiType이 추가되어 있지만 memberId가 추가
                // 안되어 있을 경우
                val mIdList = emoji.mIds
                mIdList!!.add(memberId)
                emoji = Emoji(count = emoji.count + 1, mIds = mIdList)
            }

            if (it[emojiType.key] != emoji) {  // 이전 chat.emoji 모델과 데이터가 다를 경우
                val copyEmoji: HashMap<String, Emoji> = HashMap()
                copyEmoji.putAll(it)
                copyEmoji[emojiType.key] = emoji
                firestoreWrapper.updateEmoji(chat.copy(emoji = copyEmoji))
            }
        } ?: run {
            // 메세지에 이모지가 있는 경우
            firestoreWrapper.updateEmoji(chat.copy(emoji = hashMapOf(emojiType.key to Emoji(1, arrayListOf(memberId)))))
        }
    }

    /**
     * 상대방 메세지의 추가한 이모지 리액션을 삭제합니다.
     *
     * [Chat.memberId]와 [memberId] 가 동일할 경우 자신의 메세지로 판단하여 이모지를 삭제하지 않습니다.
     *
     * [Chat.emoji]가 null 이거나 [emojiType]의 데이터가 없을 경우 이모지를 삭제하지 않습니다.
     * @param[chat] 이모지 리액션을 삭제할 메세지
     * @param[memberId] 자신의 id
     * @param[emojiType] 삭제할 EmojiType
     */
    fun deleteEmoji(chat: Chat, memberId: String, emojiType: EmojiType) {
        if (chat.memberId == memberId) {
            LoggerHelper.de("자신의 메세지는 이모지 리액션을 삭제할 수 없습니다.")
            return
        }

        if(chat.emoji == null) {
            LoggerHelper.de("삭제할 이모지가 존재하지 않습니다.")
            return
        }

        var emoji: Emoji? = chat.emoji!![emojiType.key]

        if(emoji == null) {
            LoggerHelper.de("삭제할 이모지가 존재하지 않습니다.")
            return
        }
        emoji.mIds?.let {
            if (it.contains(memberId)) { // emojiType의 이모지가 존재하고 emoji.mIds에 memberId가
                // 존재할 경우
                val mIdList = it
                mIdList.remove(memberId)
                emoji = Emoji(emoji!!.count - 1, mIdList)
            }
        }

        if (chat.emoji!![emojiType.key] != emoji) {  // 이전 chat.emoji 모델과 데이터가 다를 경우
            val copyEmoji: HashMap<String, Emoji> = HashMap()
            copyEmoji.putAll(chat.emoji!!)
            copyEmoji[emojiType.key] = emoji!!
            firestoreWrapper.updateEmoji(chat.copy(emoji = copyEmoji))
        }
    }

    /**
     * 해당 채널의 AMA상태를 확인하기 위한 fuction입니다. 주로 메세지를 전송하기전에 사용합니다.
     * @return[Ama] 현재 채널의 AMA 상태를 가지고있는 object를 전달합니다.
     */
    fun getAmaStatus() : Ama? {
        return realtimeDatabaseWrapper.ama
    }

    /**
     * 전송에 실패한 메세지를 확인하기 위한 function입니다.
     * @return[ArrayList] 전송에 실패한 [Chat] 메세지 리스트 입니다.
     */
    fun getFailedMessages() : ArrayList<Chat>? {
        return chatDao.getAllMessage()?.let { ArrayList(it) }
    }

    /**
     * 코인라이브 채팅에 사용되는 메모리를 삭제합니다. 채널을 전환하거나 채팅을 종료할 때 필수로 호출해주세요.
     */
    fun close() {
        realtimeDatabaseWrapper.close()
        firestoreWrapper.close()
    }

    private fun sendMessage(message: String, myInfo: CustomerUser, urlList: ArrayList<String>?) {
        if (urlList != null && urlList.size > 10) {
            throw SendMessageException("이미지 10개 이상 보낼수 없습니다.")
        }

        if (urlList == null && message.trim().isEmpty()) {
            throw SendMessageException("message is Empty")
        }

        if (checkAmaProceeding()) { // ama 진행 체크
            throw SendMessageException("ama 상태에서는 메세지를 보낼수 없습니다.", isAma = true)
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
            st = firestoreWrapper.getServerTimeStamp(), appName = customerName, symbol = coinSymbol, coinId = coinId,
            insertTime = CalendarHelper.nowCalendar().timeInMillis, messageType = MessageType.USER.toLowName(),
            chattingLocale = getAppResourceLocale(), memberId = myInfo.id, images = urlList
        )
    }

    private fun getAppResourceLocale(): String {
        val locale = context.resources.configuration.locales[0]
        return locale.toString().substring(0, 2)
    }

    /**
     * 로컬 디비에서 오늘 자정 기준으로 부터 7일 이전 전송 실패 데이터를 삭제합니다.
     */
    private fun deleteOldFailMessage() {
        chatDao.deleteOldMessage(CalendarHelper.getTodayMidnightTimeStamp())
    }

}
