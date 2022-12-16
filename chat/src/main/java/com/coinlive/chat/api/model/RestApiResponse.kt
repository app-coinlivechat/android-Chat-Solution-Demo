package com.coinlive.chat.api.model

data class RestApiResponse<T>(
    val code: String,
    val msg:String,
    val d:T?
) {
    fun isSuccess() : Boolean {
        return code == "SUCCESS"
    }
}


