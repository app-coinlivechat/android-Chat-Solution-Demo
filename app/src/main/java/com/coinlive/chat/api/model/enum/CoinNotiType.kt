package com.coinlive.chat.api.model.enum

/**
 * 코인라이브 채팅의 메세지를 필터링 하기 위한 enum class입니다.
 */
enum class CoinNotiType {
    /**
     * 코인라이브 채팅 메세지 중 Twitter 메세지에 해당합니다.
     */
    CHAT_MEDIUM,

    /**
     * 코인라이브 채팅 메세지 중 Medum 메세지에 해당합니다.
     */
    CHAT_TWITTER,

    /**
     * 코인라이브 채팅 메세지 중 Long/Short 메세지에 해당합니다.
     */
    CHAT_LIQUIDATION
}
