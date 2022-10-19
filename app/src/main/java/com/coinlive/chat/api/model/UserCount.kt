package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class UserCount(
    @SerializedName("cnt") val count:Int,
    val status:String?
)
