package com.coinlive.chat.firebase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coinlive.chat.util.MessageIdHelper
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

@Entity
data class Chat(
    @PrimaryKey val messageId:String = MessageIdHelper.generateMessageId(),
    @SerializedName("sym") val symbol:String,
    @SerializedName("aid") val firebaseAuthId:String?= null,
    @SerializedName("cid") val coinId:String,
    @SerializedName("mid") val memberId:String?= null,
    @SerializedName("un") val userNickname:String?= null,
    @SerializedName("url") val profileUrl:String?= null,
    @SerializedName("t") val insertTime:Long,
    @SerializedName("mko") val koMessage:String,
    @SerializedName("men") val enMessage:String,
    @SerializedName("co") val chattingLocale:String,
    @SerializedName("mt") val messageType:String,
    @SerializedName("nft") val isNFTProfile:Boolean = false,
    @SerializedName("ex") val exchange:String?= null,
    @SerializedName("rp_mt") val replyInsertTime:Int = 0,
    @SerializedName("rp_msg_id") val replyMessageId:String? = null,
    @SerializedName("rp_un") val replyUserNickName:String?= null,
    @SerializedName("rp_mko") val replyKoMessage:String?= null,
    @SerializedName("rp_men") val replyEnMessage:String?= null,
    @SerializedName("rp_mid") val replyUserId:String?= null,
    @SerializedName("lab") val label:String?= null,
    @SerializedName("emo") val emoji:HashMap<String, Emoji>?= null,
    @SerializedName("img") val images:ArrayList<String>?= null,
    @SerializedName("isHolder") val isHolder:Boolean = false,
    @SerializedName("asset") val asset: Asset?= null,
    @SerializedName("st") val st:Timestamp?= null
)
