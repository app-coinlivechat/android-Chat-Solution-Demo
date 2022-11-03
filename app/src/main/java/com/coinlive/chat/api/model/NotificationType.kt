package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

/**
 * 코인라이브 채팅의 필터링 정보 리스트를 관리하는 data class 입니다.
 */
data class NotificationTypeList(
    /**
     * 채팅의 필터링 정보 리스트입니다.
     */
    @SerializedName("ntList") val typeList: List<NotificationType>,
)

/**
 * 코인라이브 채팅의 필터링 정보를 관리하는 data class 입니다.
 */
data class NotificationType(
    /**
     * 필터의 이름입니다.
     */
    val name: String,
    /**
     * 필터의 종류입니다.
     */
    val type: String,
    /**
     * 필터를 포함하는 상위 이름입니다.
     */
    @SerializedName("tg") val tag: String,
    /**
     * 필터의 id입니다.
     */
    @SerializedName("ntid") val id: String,
)

data class NotificationMap(
    @SerializedName("noti") val notiMap: HashMap<String, Boolean>,
)