package com.coinlive.chat.firebase.model.enum

enum class MessageType {
    USER,
    USER_RP,
    CM,
    CM_RP,
    ADMIN,
    ADMIN_RP,
    TWITTER,
    BUY,
    SELL,
    JUMP,
    DROP,
    MEDIUM,
    ASSET;
    fun toLowName() : String {
        return name.lowercase()
    }

}