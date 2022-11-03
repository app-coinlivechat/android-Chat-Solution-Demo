package com.coinlive.chat.firebase.listener

import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.model.Chat

/**
 * [CoinliveChat.coinId]의 신규,삭제,수정,로드 등 메세지들의 이벤트 listener
 */
interface MessageListener {
    /**
     * 로드된 메세지 중 삭제된 메세지를 전달합니다.
     * @param[chat] 삭제된 메세지의 [Chat] object가 전달 됩니다.
     */
    fun deletedMessage(chat: Chat)

    /**
     * 로드된 메세지 중 수정된 메세지를 전달합니다.
     * 이모지 리액션이 추가,삭제 될 경우 발생합니다.
     * @param[chat] 수정된 메세지의 [Chat] object가 전달 됩니다.
     */
    fun modifyMessage(chat: Chat)

    /**
     * 로드된 메세지 리스트를 전달합니다.
     * [CoinliveChat.reloadMessages]를 요청할 경우 [isReload] 값은 true로 전달 됩니다.
     * @param[chatList] 로드된 메세지 리스트를 전달합니다.
     * @param[isReload] 다시 로드된 메세지 리스트의 상태값을 전달합니다.
     */
    fun oldMessages(chatList: ArrayList<Chat>, isReload: Boolean)

    /**
     * 새로 추가된 메세지를 전달합니다.
     * @param[chat] 추가된 메세지의 [Chat] object가 전달 됩니다.
     */
    fun newMessages(chat: Chat)

    /**
     * 전송 실패된 메세지를 전달합니다.
     * [CoinliveChat.retrySendMessage]를 요청해서 실패할 경우에도 발생됩니다.
     * @param[chat] 전송 실패된 메세지의 [Chat] object가 전달 됩니다.
     */
    fun failSendMessage(chat: Chat)

    /**
     * 재 전송 실패된 메세지를 전달합니다.
     * @param[messageId] 재 전송 실패된 메세지의 id가 전달됩니다.
     */
    fun retrySendMessageSuccess(messageId: String)
}