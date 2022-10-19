package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

data class EmojiChat(
    @SerializedName("cnt") val count:Int,
    @SerializedName("mid") val mIds:List<String>
)
