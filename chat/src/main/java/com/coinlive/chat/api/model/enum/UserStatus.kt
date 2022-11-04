package com.coinlive.chat.api.model.enum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 코인라이브 채팅 사용자의 상태를 표시하는 enum class입니다.
 */
@Parcelize
enum class UserStatus : Parcelable {
    /**
     * 활성화된 사용자입니다.
     */
    ACTIVE,

    /**
     * 정지 대상인 사용자입니다.
     */
    BLOCK,

    /**
     * 휴면 사용자입니다.
     */
    DORMANT,

    /**
     * 비활성화된 사용자입니다.
     */
    INACTIVE,

    /**
     * none or etc
     */
    NONE
}