package com.coinlive.chat.api.model

import com.coinlive.chat.api.model.enum.ResponseCode

data class RestApiResponse<T>(
    val code: ResponseCode,
    val msg:String,
    val d:T?
) {
    fun isSuccess() : Boolean {
        return code == ResponseCode.SUCCESS
    }
}


