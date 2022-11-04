package com.coinlive.chat.api.model

import android.os.Parcelable
import com.coinlive.chat.api.model.enum.UserStatus
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 코인라이브 채팅의 일반 사용자 정보를 관리하는 struct입니다.
 */
@Parcelize
data class CustomerUser(
    /**
     * 사용자의 정지 시간입니다.
     */
    @SerializedName("bt") val blockTime:String?,
    /**
     * Customer의 id입니다.
     */
    @SerializedName("ccid") val customerId:String,
    /**
     * 사용자의 파이어베이스 UUID입니다.
     */
    @SerializedName("fbid") val firebaseUuid:String,
    /**
     * 사용자의 이미지(디폴트 유저 이미지)입니다.
     */
    @SerializedName("img") val profileImage:String,
    /**
     * 사용자의 UUID입니다.
     */
    @SerializedName("mid") val id:String,
    /**
     * 사용자의 닉네임입니다.
     */
    @SerializedName("nik") val nickName:String,
    /**
     * 사용자의 차단 사용자 id 리스트입니다.
     */
    @SerializedName("block") val blockUserMidList:ArrayList<String>,
    val status: UserStatus
):Parcelable
