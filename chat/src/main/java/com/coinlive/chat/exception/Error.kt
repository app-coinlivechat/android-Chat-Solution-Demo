package com.coinlive.chat.exception

enum class Error(val msg: String,val code: Int) {

    UNKNOWN("unknown reason",200),

    // Network
    NETWORK("rest api 통신 에러",999),
    REQUEST_FAIL("rest api 요청 실패",1001),

    // Firebase
    NO_AUTH_USER("firebase authentication에서 id token이 null 입니다.",1000),
    SEND_MESSAGE("Send message error",2004),
    FETCH_MESSAGE("",2005),
    QUERY_FETCH_MESSAGE("",2006),
    CHAT_AMA("",2007),
    UPLOAD_IMAGE("이미지는 15MB를 넘을 수 없습니다.",2008),


}