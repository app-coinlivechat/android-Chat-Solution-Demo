package com.coinlive.chat.exception

import com.coinlive.chat.api.model.enum.ResponseCode


abstract class CoinliveException(message: String, val code: Int) : Exception(message)

class NetworkException(message: String) : CoinliveException(
    "$message NetworkException : ${Error.NETWORK.msg}", Error
        .NETWORK
        .code
)

class RequestFailException(message: String, respCode: ResponseCode, respMsg: String) : CoinliveException
    ("RequestFailException : $respCode, $respMsg\n $message", Error.REQUEST_FAIL.code)

class FirebaseIdTokenException(message: String) :
    CoinliveException("$message FirebaseAuthException : ${Error.ID_TOKEN.msg}", Error.ID_TOKEN.code) {
}