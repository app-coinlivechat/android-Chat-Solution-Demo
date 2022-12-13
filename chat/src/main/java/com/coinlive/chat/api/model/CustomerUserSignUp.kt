package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class CustomerUserSignUpBody(
    @SerializedName("ccid") val customerId:String,
    @SerializedName("img") val profileUrl:String,
    @SerializedName("nik") val nickName:String
)

/**
 * 코인라이브 채팅에 가입한 정보를 관리하는 data class 입니다.
 */
data class CustomerUserSignUp(
    /**
     * Firebase UUID입니다.
     */
    @SerializedName("fbid") val firebaseUuid:String,
    /**
     * Custom token입니다.
     */
    @SerializedName("token")val customToken:String
)

data class CustomTokenBody(
    @SerializedName("mid") val id:String,
    @SerializedName("name") val customerName:String,
    @SerializedName("pw") val passWord:String,
)
