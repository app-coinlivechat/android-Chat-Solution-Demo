package com.coinlive.chat.api.model

import com.coinlive.chat.api.model.enum.UserStatus
import com.google.gson.annotations.SerializedName

data class CustomerUser(
    @SerializedName("bt") val blockTime:String?,
    @SerializedName("ccid") val customerId:String,
    @SerializedName("fbid") val firebaseUuid:String,
    @SerializedName("img") val profileImage:String,
    @SerializedName("mid") val id:String,
    @SerializedName("nik") val nickName:String,
    val status: UserStatus
)
