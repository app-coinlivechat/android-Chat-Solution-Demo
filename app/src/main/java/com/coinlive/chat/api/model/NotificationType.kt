package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class NotificationTypeList(
    @SerializedName("ntList")
    val typeList:List<NotificationType>
)

data class NotificationType(
    val name:String,
    val type:String,
    @SerializedName("tg") val tag:String,
    @SerializedName("ntid") val id:String
)