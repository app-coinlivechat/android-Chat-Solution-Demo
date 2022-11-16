package com.coinlive.uikit.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Notification(
    val id:String,
    val name : String,
    var enable : Boolean
):Parcelable
