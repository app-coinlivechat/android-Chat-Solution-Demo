package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class ChannelList(
    @SerializedName("clist") val list:List<Channel>
)

data class Channel(
    @SerializedName("cid") val coinId:String,
    @SerializedName("ico") val coinUrl:String?,
    val name:String?,
    @SerializedName("sym") val coinSymbol:String
)
