package com.coinlive.chat.api

import com.coinlive.chat.exception.CoinliveException

interface ResponseCallback<T>  {
    fun onSuccess(value:T)
    fun onFail(exception: CoinliveException)
}