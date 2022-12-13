package com.coinlive.chat.firebase.listener

import android.net.Uri

interface DynamicLinkListener {
    fun onSuccess(uri : Uri)
    fun onFail(exception: java.lang.Exception)
}