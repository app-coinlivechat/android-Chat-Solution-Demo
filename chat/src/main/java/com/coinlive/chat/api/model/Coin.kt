package com.coinlive.chat.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 코인라이브의 개별 코인 정보를 관리하는 data class 입니다.
 */
@Parcelize
data class Coin(
    /**
     * 코인의 id 입니다.
     */
    @SerializedName("cid") val id:String,
    /**
     * 코인의 이미지입니다.
     */
    @SerializedName("ico") val iconUrl:String?,
    /**
     * 코인의 이름입니다.
     */
    val name:String?,
    /**
     * coin의 심볼입니다.
     */
    @SerializedName("sym") val symbol:String,
    /**
     * coin의 ama상태입니다.
     */
    @SerializedName("ama") val isAma:Boolean?,
    /**
     * coin의 ama 시작 시간입니다.
     */
    @SerializedName("amat") val amaTime:String?,
    /**
     * 현재 해당 코인 채팅방에 접속해있는 사용자의 수 입니다.
     */
    @SerializedName("pcnt") val userCount:Int,
    /**
     * 사용자의 필터링 정보입니다.
     */
    @SerializedName("noti") val notificationMap:Map<String,Boolean>,
):Parcelable
