package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

data class Ama(
    @SerializedName("pause") val isPause:Boolean,
    @SerializedName("start") val startTime:Long,
    @SerializedName("end") val endTime:Long?,
    @SerializedName("heart") val heartCount:Int
)
