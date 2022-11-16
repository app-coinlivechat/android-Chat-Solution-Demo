package com.coinlive.chat

import java.util.*

class Coinlive {
    companion object {
        /**
         * 테스트 환경을 위해 현재 build mode를 설정합니다.
         * 디폴트 값은 true 입니다.
         * release mode로 build하기 위해서는 반드시 값을 false로 변경해야 합니다.
         */
        var isDebug: Boolean = true

        /**
         * RestAPI, Chatting Message ,etc.. 에서 사용합니다.
         * 설정 값에 따라 언어 보여지는 언어가 달라질 수 있으니
         * 앱에서 사용하고 있는 locale 값과 동일한 값으로 설정해야 합니다.
         */
        var locale: Locale = Locale.getDefault()


    }
}