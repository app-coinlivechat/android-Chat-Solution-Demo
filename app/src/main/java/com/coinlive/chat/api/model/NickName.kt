package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

/**
 * nickname 수정/확인 등에 관련해 정보를 관리하는 data class 입니다.
 */
data class NickName(
    /**
     * 금칙어입니다.
     */
    val word:String,
)

data class NickNameBody(
    @SerializedName("nik") val nickName:String,
    @SerializedName("ccid") val customerId:String,
)