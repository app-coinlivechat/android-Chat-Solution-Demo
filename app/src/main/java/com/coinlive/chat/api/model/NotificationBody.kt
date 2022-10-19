package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class NotificationBody(
    @SerializedName("cid") val coinId:String,
    val type:String?
)
