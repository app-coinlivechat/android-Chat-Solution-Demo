package com.coinlive.chat.api.model

import com.coinlive.chat.api.model.enum.UserStatus
import com.google.gson.annotations.SerializedName

/**
 * 코인라이브 채팅 사용자의 수 정보를 관리하는 data class 입니다.
 */
data class UserCount(
    /**
     * 채팅 사용자의 수 입니다.
     */
    @SerializedName("cnt") val count:Int,
    /**
     * 로그인한 사용자의 상태를 표시합니다.
     * @see UserStatus
     */
    val status: UserStatus?
)
