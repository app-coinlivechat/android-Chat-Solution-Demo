package com.coinlive.chat.firebase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coinlive.chat.util.MessageIdHelper
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import com.coinlive.chat.firebase.model.enum.MessageType

/**
 * 채팅 메세지 모델 클래스
 */
@Entity
data class Chat(
    /**
     * 메세지 id
     */
    @PrimaryKey val messageId: String = MessageIdHelper.generateMessageId(),
    /**
     * 채팅 메세지가 발생된 코인 심볼
     */
    @SerializedName("sym") val symbol: String,
    /**
     * 전송한 사용자의 firebase Authentication id
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @SerializedName("aid") val firebaseAuthId: String? = null,
    /**
     * 채팅 메세지가 발생된 코인 id
     */
    @SerializedName("cid") val coinId: String,
    /**
     * 전송한 사용자의 id
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @SerializedName("mid") val memberId: String? = null,
    /**
     * 전송한 사용자의 닉네입
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @SerializedName("un") val userNickname: String? = null,
    /**
     * 전송한 사용자의 프로필 url
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @SerializedName("url") val profileUrl: String? = null,
    /**
     * 전송 TimeStamp 값
     * TimeStamp값은 UTC 기준입니다.
     */
    @SerializedName("t") val insertTime: Long,
    /**
     * 한국 메세지
     */
    @SerializedName("mko") val koMessage: String,
    /**
     * 영문 메세지
     */
    @SerializedName("men") val enMessage: String,
    /**
     * 메세지 locale 정보
     */
    @SerializedName("co") val chattingLocale: String,
    /**
     * 메세지 타입
     * [MessageType]을 참고하세요.
     */
    @SerializedName("mt") val messageType: String,
    /**
     * 전송한 사용자의 nft profile 유무 값
     */
    @SerializedName("nft") val isNFTProfile: Boolean = false,
    /**
     * 전송한 사용자의 AppName
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @SerializedName("ex") val appName: String? = null,
    /**
     * 답장할 메세지의 전송 시간 TimeStamp 값
     * TimeStamp값은 UTC 기준입니다.
     * 답장 메세지가 아닐경우 0 입니다.
     */
    @SerializedName("rp_mt") val replyInsertTime: Int = 0,
    /**
     * 답장할 메세지의 id
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @SerializedName("rp_msg_id") val replyMessageId: String? = null,
    /**
     * 답장할 메세지의 사용자 닉네임
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @SerializedName("rp_un") val replyUserNickName: String? = null,
    /**
     * 답장할 메세지의 국문 메세지
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @SerializedName("rp_mko") val replyKoMessage: String? = null,
    /**
     * 답장할 메세지의 영문 메세지
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @SerializedName("rp_men") val replyEnMessage: String? = null,

    /**
     * 답장할 메세지의 사용자 id
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @SerializedName("rp_mid") val replyUserId: String? = null,
    /**
     * 메세지 라벨
     * AMA 진행 중 보내진 메세지라면 "AMA" 값이 전달 되고 그 이외일 경우 null 입니다.
     */
    @SerializedName("lab") val label: String? = null,
    /**
     * 메세지 이모지 리액션
     */
    @SerializedName("emo") val emoji: HashMap<String, Emoji>? = null,
    /**
     * 이미지 메세지의 url 리스트
     * 이미지 메세지가 아닐 경우 null 입니다.
     */
    @SerializedName("img") val images: ArrayList<String>? = null,
    /**
     * 전송 사용자의 홀더 유무 값
     */
    @SerializedName("isHolder") val isHolder: Boolean = false,
    /**
     * 자산 인증
     * 메세지 타임이 [MessageType.ASSET]이 아닐 경우 null 입니다.
     */
    @SerializedName("asset") val asset: Asset? = null,
    /**
     * 서버 타임스템프 값
     * TimeStamp값은 UTC 기준입니다.
     */
    @SerializedName("st") val st: Timestamp? = null,
)
