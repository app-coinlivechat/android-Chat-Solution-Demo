package com.coinlive.chat.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 코인라이브 채팅의 채널 리스트 정보를 관리하는 data class 입니다.
 */
data class ChannelList(
    /**
     * 코인라이브 채팅의 채널 리스트입니다.
     */
    @SerializedName("clist") val list:List<Channel>
)

@Parcelize
data class Channel(
    @SerializedName("cid") val coinId:String,
    @SerializedName("ico") val coinUrl:String?,
    val name:String?,
    @SerializedName("sym") val coinSymbol:String
):Parcelable
