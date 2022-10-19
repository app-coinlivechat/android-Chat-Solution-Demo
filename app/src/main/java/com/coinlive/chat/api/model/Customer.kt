package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class Customer(
    val appId:String,
    @SerializedName("ccid") val id:String,
    @SerializedName("img") val defaultImageUrl:String,
    val name:String
)
