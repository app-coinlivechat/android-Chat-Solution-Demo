package com.coinlive.chat

object CoinliveChat {
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
