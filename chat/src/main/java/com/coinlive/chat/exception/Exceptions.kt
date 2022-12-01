package com.coinlive.chat.exception

import com.coinlive.chat.api.model.enums.ResponseCode


open class CoinliveException(message: String, val code: Int) : Exception(message)


class FetchMessageException(message: String) : CoinliveException(
    "$message FetchMessageException : ${Error.FETCH_MESSAGE.msg}", Error
        .FETCH_MESSAGE
        .code
)

class UploadImageException(message: String) : CoinliveException(
    "$message UploadImageException : ${Error.UPLOAD_IMAGE.msg}", Error
        .UPLOAD_IMAGE
        .code
)

class UnknwonExecption(message: String) : CoinliveException(
    "$message UnknwonExecption : ${Error.UNKNOWN.msg}", Error
        .UNKNOWN
        .code
)

class NetworkException(message: String) : CoinliveException(
    "$message NetworkException : ${Error.NETWORK.msg}", Error
        .NETWORK
        .code
)

class RequestFailException(message: String, respCode: ResponseCode, respMsg: String) : CoinliveException
    ("RequestFailException : $respCode, $respMsg\n $message", Error.REQUEST_FAIL.code)

class FirebaseIdTokenException(message: String) :
    CoinliveException("$message FirebaseAuthException : ${Error.NO_AUTH_USER.msg}", Error.NO_AUTH_USER.code)

class SendMessageException(message: String, isAma: Boolean = false) :
    CoinliveException("$message SendMessageException : ${if (isAma) Error.CHAT_AMA.msg else Error.SEND_MESSAGE.msg}",
        if (isAma) Error.CHAT_AMA.code else Error.SEND_MESSAGE.code)



