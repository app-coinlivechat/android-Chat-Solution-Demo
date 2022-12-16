package com.coinlive.chat.firebase.listener

import android.net.Uri
import androidx.annotation.Keep

@Keep
interface DynamicLinkListener {
    fun onSuccess(uri : Uri)
    fun onFail(exception: java.lang.Exception)
}