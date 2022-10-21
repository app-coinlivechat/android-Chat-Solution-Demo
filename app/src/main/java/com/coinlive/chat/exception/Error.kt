package com.coinlive.chat.exception

enum class Error(val msg: String,val code: Int) {
    NETWORK("rest api 통신 에러",999),
    ID_TOKEN("firebase authentication에서 id token이 null 입니다.",1000),
    REQUEST_FAIL("rest api 요청 실패",1001)
}