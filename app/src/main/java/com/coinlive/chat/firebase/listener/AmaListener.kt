package com.coinlive.chat.firebase.listener

import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.model.Ama

/**
 * [CoinliveChat.coinId]의 AMA 진행 상태를 확인 하기 위한 Listener
 */
interface AmaListener{
    /**
     * [CoinliveChat.coinId]의 AMA 진행 상태를 전달합니다.
     * 채팅방 진입시 AMA에 대한 정보가 없을 경우 이벤트가 발생되지 않습니다.
     * @param[ama] 진행 중인 [Ama] object를 전달 합니다.
     */
    fun getAma(ama: Ama)
}