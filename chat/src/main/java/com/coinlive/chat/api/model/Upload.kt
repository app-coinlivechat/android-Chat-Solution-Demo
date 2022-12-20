package com.coinlive.chat.api.model

import androidx.annotation.Keep

/**
 * Image URL 정보를 관리하는 data class 입니다.
 */
@Keep
data class Upload(
    /**
     * 업로드 된 image URL입니다.
     */
    val url:String
)
