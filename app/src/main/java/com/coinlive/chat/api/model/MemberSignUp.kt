package com.coinlive.chat.api.model

import com.coinlive.chat.api.model.enum.UserStatus
import com.google.gson.annotations.SerializedName

data class MemberSignupCheck (
    val status: UserStatus,
    @SerializedName("bt") val blockEndTime:String?,
    @SerializedName("bp") val blockPeriodTime:String?,
)

data class MemberSignupCheckBody(
    @SerializedName("fid") val firebaseUuid:String
)