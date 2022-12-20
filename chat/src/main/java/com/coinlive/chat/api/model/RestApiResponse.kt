package com.coinlive.chat.api.model

import androidx.annotation.Keep

@Keep
data class RestApiResponse<T>(
    val code: String,
    val msg:String,
    val d:T?
) {
    fun isSuccess() : Boolean {
        return code == "SUCCESS"
    }
}


