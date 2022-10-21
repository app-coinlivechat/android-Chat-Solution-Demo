package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class Coin(
    @SerializedName("cid") val id:String,
    @SerializedName("ico") val iconUrl:String?,
    val name:String?,
    @SerializedName("sym") val symbol:String,
    @SerializedName("ama") val isAma:Boolean?,
    @SerializedName("amat") val amaTime:String?,
    @SerializedName("pcnt") val userCount:Int,
    @SerializedName("noti") val notificationMap:Map<String,Boolean>,
)
