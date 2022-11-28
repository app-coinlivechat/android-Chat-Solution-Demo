package com.coinlive.uikit.models

import android.graphics.drawable.Drawable

data class EmojiItem(
    val key:String,
    val count:Int,
    val resource:Drawable,
    var isSelect:Boolean = false
)
