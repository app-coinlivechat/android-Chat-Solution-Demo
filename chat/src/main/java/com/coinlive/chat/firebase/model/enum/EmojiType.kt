package com.coinlive.chat.firebase.model.enum

import androidx.annotation.Keep

/**
 * 이모지 타입
 */
@Keep
enum class EmojiType(val key:String) {
    GOOD(":+1:"),
    HEART(":heart:"),
    CLAP(":clap:"),
    ROCKET(":rocket:"),
    CRY(":cry:"),
    ASTONISHED(":astonished:");
}