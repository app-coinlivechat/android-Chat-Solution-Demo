package com.coinlive.chat.api.model

import com.coinlive.chat.api.model.enum.UserStatus
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("des") val description:String?,
    @SerializedName("flwng") val followingCount:Int,
    @SerializedName("myFlwng") val isFollowingMe:Boolean,
    @SerializedName("flwr") val followerCount:Int,
    @SerializedName("img") val profileUrl:String,
    @SerializedName("mid") val userId:String,
    @SerializedName("nik") val nickName:String,
    @SerializedName("noti") val isNotification:Boolean,
    @SerializedName("sns") val sns:Sns?,
    @SerializedName("nft") val isNFTProfile:Boolean
)

data class MyInfo(
    @SerializedName("block") val blockUserList:List<String> ,
    @SerializedName("ex") val exchangeOrder:ExchangeOrder,
    @SerializedName("co") val countryCode:String ,
    @SerializedName("ct") val singupTime:String,
    @SerializedName("cur") val currency:String ,
    @SerializedName("des") val description:String?,
    @SerializedName("fbId") val firebaseUuid:String ,
    @SerializedName("flwng") val followingCount:Int,
    @SerializedName("flwr") val followerCount:Int,
    @SerializedName("img") val profileUrl:String,
    @SerializedName("leftAt") val leftTime:String?,
    @SerializedName("mid") val id:String,
    @SerializedName("nik") val nickName:String,
    @SerializedName("roles") val roles:List<String>,
    @SerializedName("sns") val sns:Sns?,
    @SerializedName("subCnt") val subChannelCount:Int,
    @SerializedName("thm") val appTheme:String,
    val status:UserStatus,
    @SerializedName("nft") val sNFTProfile:Boolean
)

data class Sns(
    val youtube:String?,
    val twitter:String?
)

data class ExchangeOrder(
    val krw:List<String>,
    val usd:List<String>
)
