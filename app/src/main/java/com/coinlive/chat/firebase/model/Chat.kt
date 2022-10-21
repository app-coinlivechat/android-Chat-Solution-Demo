package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

data class Chat(
    var messageId:String?,
    @SerializedName("sym") val symbol:String,
    @SerializedName("aid") val firebaseAuthId:String?,
    @SerializedName("cid") val coinId:String,
    @SerializedName("mid") val memberId:String?,
    @SerializedName("un") val userNickname:String?,
    @SerializedName("url") val profileUrl:String?,
    @SerializedName("t") val insertTime:String,
    @SerializedName("mko") val koMessage:String,
    @SerializedName("men") val enMessage:String,
    @SerializedName("co") val chattingLocale:String,
    @SerializedName("mt") val messageType:String,
    @SerializedName("nft") val isNFTProfile:Boolean = false,
    @SerializedName("ex") val exchange:String = "",
    @SerializedName("rp_mt") val replyInsertTime:Int,
    @SerializedName("rp_msg_id") val replyMessageId:String?,
    @SerializedName("rp_un") val replyUserNickName:String?,
    @SerializedName("rp_mko") val replyKoMessage:String?,
    @SerializedName("rp_men") val replyEnMessage:String?,
    @SerializedName("rp_mid") val replyUserId:String?,
    @SerializedName("lab") val label:String?,
    @SerializedName("emo") val emoji:Map<String, Emoji>?,
    @SerializedName("img") val images:List<String>?,
    @SerializedName("isHolder") val isHolder:Boolean = false,
    @SerializedName("asset") val asset: Asset?,
    @SerializedName("st") val st:Any?
)
