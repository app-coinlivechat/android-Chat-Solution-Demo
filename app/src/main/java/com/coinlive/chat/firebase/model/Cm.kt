package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

data class Cm(
    @SerializedName("aid") val cmFirebaseUuid:String,
    @SerializedName("msg") val message:String?,
    @SerializedName("t") val insertTime:Int
)