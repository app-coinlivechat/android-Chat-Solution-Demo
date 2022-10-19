package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class NickName(
    val word:String,
)

data class NickNameBody(
    @SerializedName("nik") val nickName:String,
)