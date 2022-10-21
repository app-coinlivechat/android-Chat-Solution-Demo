package com.coinlive.chat.api.model

import com.coinlive.chat.api.model.enum.UserStatus
import com.google.gson.annotations.SerializedName

data class UserCount(
    @SerializedName("cnt") val count:Int,
    val status: UserStatus?
)
