package com.coinlive.chat.exception

import androidx.annotation.Keep

@Keep
open class CoinliveException(message: String, val code: Int) : Exception(message)

@Keep
class FetchMessageException(message: String) : CoinliveException(
    "$message FetchMessageException : ${Error.FETCH_MESSAGE.msg}", Error
        .FETCH_MESSAGE
        .code
)
@Keep
class UploadImageException(message: String) : CoinliveException(
    "$message UploadImageException : ${Error.UPLOAD_IMAGE.msg}", Error
        .UPLOAD_IMAGE
        .code
)
@Keep
class UnknwonExecption(message: String) : CoinliveException(
    "$message UnknwonExecption : ${Error.UNKNOWN.msg}", Error
        .UNKNOWN
        .code
)
@Keep
class NetworkException(message: String) : CoinliveException(
    "$message NetworkException : ${Error.NETWORK.msg}", Error
        .NETWORK
        .code
)
@Keep
class RequestFailException(respCode: String, respMsg: String) : CoinliveException
    ("$respCode, $respMsg", Error.REQUEST_FAIL.code)
@Keep
class FirebaseIdTokenException(message: String) :
    CoinliveException("$message FirebaseAuthException : ${Error.NO_AUTH_USER.msg}", Error.NO_AUTH_USER.code)
@Keep
class SendMessageException(message: String, isAma: Boolean = false) :
    CoinliveException("$message SendMessageException : ${if (isAma) Error.CHAT_AMA.msg else Error.SEND_MESSAGE.msg}",
        if (isAma) Error.CHAT_AMA.code else Error.SEND_MESSAGE.code)
@Keep
class DynamicLinkException(message : String?) : CoinliveException("$message\n${Error.DYNAMIC_LINK.msg}", Error
    .DYNAMIC_LINK.code)



