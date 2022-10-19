package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class CustomerUserSignUpBody(
    @SerializedName("ccid") val customerId:String,
    @SerializedName("img") val profileUrl:String,
    @SerializedName("nik") val nickName:String
)

data class CustomerUserSignUpResponse(
    @SerializedName("fbid") val firebaseUuid:String,
    @SerializedName("token")val customToken:String
)

data class CustomTokenBody(
    @SerializedName("mid") val id:String,
)
