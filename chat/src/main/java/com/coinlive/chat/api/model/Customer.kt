package com.coinlive.chat.api.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 코인라이브 Chat solution을 사용하는 고객(Service)사의 정보를 관리하는 data class 입니다.
 */
@Parcelize
@Keep
data class Customer(
    /**
     * 고객사의 app id입니다.
     */
    val appId:String,
    /**
     * 고객사의 ccid입니다.
     */
    @SerializedName("ccid") val id:String,
    /**
     * 고객사의 이미지입니다.
     */
    @SerializedName("img") val defaultImageUrl:String,
    /**
     * 고객사의 이름입니다.
     */
    val name:String
) : Parcelable
