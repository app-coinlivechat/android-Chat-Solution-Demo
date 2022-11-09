package com.coinlive.chat.firebase.model.enum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [Chat] 메세지 타입.
 */
@Parcelize
enum class MessageType : Parcelable {
    /**
     * 일반 사용자
     */
    USER,

    /**
     * 일반 사용자 메세지의 답장
     */
    USER_RP,

    /**
     * CM 권한 사용자 메세지
     */
    CM,

    /**
     * CM 권한 사용자 메세지의 답장
     */
    CM_RP,

    /**
     * 관리자 권한 사용자 메세지
     */
    ADMIN,

    /**
     * 관리자 권한 사용자 메세지의 답장
     */
    ADMIN_RP,

    /**
     * 오피셜 트위터 계정 트윗
     */
    TWITTER,

    /**
     * 롱 청산 메세지
     */
    BUY,

    /**
     * 숏 청산 메세지
     */
    SELL,

    /**
     * 가격 급등 메세지
     */
    JUMP,

    /**
     * 가격 급락 메세지
     */
    DROP,

    /**
     * 오피셜 미디움 계정 포스트 메세지
     */
    MEDIUM,

    /**
     * 자산 인증 메세지
     */
    ASSET;
    fun toLowName() : String {
        return name.lowercase()
    }
}