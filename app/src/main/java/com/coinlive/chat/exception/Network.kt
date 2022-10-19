package com.coinlive.chat.exception

import com.coinlive.chat.api.model.enum.ResponseCode

class NetworkException(message:String) : Exception(message) {
    override val message: String
        get() = "NetworkException : $message "
}

class RequestFailException(message:String, val code: ResponseCode, val responseMessage:String) : Exception(message) {

    override val message: String
        get() = "RequestFailException : $code, $responseMessage\n$message"
}