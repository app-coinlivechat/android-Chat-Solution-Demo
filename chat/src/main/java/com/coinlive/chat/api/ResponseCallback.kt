package com.coinlive.chat.api

import androidx.annotation.Keep
import com.coinlive.chat.exception.CoinliveException

@Keep
interface ResponseCallback<T>  {
    fun onSuccess(value:T)
    fun onFail(exception: CoinliveException)
}