package com.coinlive.chat.firebase.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Cm(
    var messageId:String? = null,

    @set:PropertyName("msg")
    @get:PropertyName("msg")
    var message:String?,


    @set:PropertyName("t")
    @get:PropertyName("t")
    var insertTime:Long
): Parcelable {
    constructor() : this("","",0)
}