package com.coinlive.chat.firebase.listener

import com.coinlive.chat.firebase.CoinliveChat

/**
 * [CoinliveChat.coinId]의 CM 공지 사항을 전달하기 위한 Listener
 */
interface CmNoticeListener {
    /**
     * 채팅방 진입시 공지 사항이 없다면 이벤트가 발생되지 않고,
     * 진입 이후 등록,수정,삭제시 이벤트가 발생됩니다.
     * 공지 사항 삭제시 [msg]는 null이 전달됩니다.
     * @param[msg] 공지사항
     */
    fun getCmNotice(msg: String?)
}