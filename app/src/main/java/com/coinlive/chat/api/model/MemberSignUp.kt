package com.coinlive.chat.api.model

import com.coinlive.chat.api.model.enum.UserStatus
import com.google.gson.annotations.SerializedName

/**
 * 코인라이브 채팅에 Firebase uid 를 가지고 있는 멤버가 존재하는지 확인하는 data class 입니다.
 */
data class MemberSignupCheck (
    /**
     * 사용자의 상태를 표시합니다.
     * @see UserStatus
     */
    val status: UserStatus,
    /**
     * 사용자의 이용정지 종료 시간
     */
    @SerializedName("bt") val blockEndTime:String?,
    /**
     * 사용자의 이용정지 기간
     */
    @SerializedName("bp") val blockPeriodTime:String?,
)

data class MemberSignupCheckBody(
    @SerializedName("fid") val firebaseUuid:String
)