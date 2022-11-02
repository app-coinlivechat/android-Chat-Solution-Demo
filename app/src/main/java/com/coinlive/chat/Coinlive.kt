package com.coinlive.chat

class Coinlive {
    companion object {
        /**
         * 테스트 환경을 위해 현재 build mode를 설정합니다.
         * 디폴트 값은 true 입니다.
         * release mode로 build하기 위해서는 반드시 값을 false로 변경해야 합니다.
         */
        var isDebug: Boolean = true
    }
}