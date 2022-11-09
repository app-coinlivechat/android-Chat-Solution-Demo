package com.coinlive.chat.firebase.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Cm(
    @set:PropertyName("aid")
    @get:PropertyName("aid")
    var cmFirebaseUuid:String? = null,

    @set:PropertyName("msg")
    @get:PropertyName("msg")
    var message:String?,


    @set:PropertyName("t")
    @get:PropertyName("t")
    var insertTime:Int
): Parcelable {
    constructor() : this("","",0)
}