package com.coinlive.chat.firebase.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coinlive.chat.util.MessageIdHelper
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import com.coinlive.chat.firebase.model.enum.MessageType
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize


/**
 * 채팅 메세지 모델 클래스
 */
@Parcelize
@Entity
@Keep
data class Chat(
    /**
     * 메세지 id
     */
    @PrimaryKey
    var messageId: String = MessageIdHelper.generateMessageId(),
    /**
     * 채팅 메세지가 발생된 코인 심볼
     */
    @set:PropertyName("sym")
    @get:PropertyName("sym")
    var symbol: String,
    /**
     * 전송한 사용자의 firebase Authentication id
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @set:PropertyName("aid")
    @get:PropertyName("aid")
    var firebaseAuthId: String? = null,
    /**
     * 채팅 메세지가 발생된 코인 id
     */
    @set:PropertyName("cid")
    @get:PropertyName("cid")
    var coinId: String,
    /**
     * 전송한 사용자의 id
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @set:PropertyName("mid")
    @get:PropertyName("mid")
    var memberId: String? = null,
    /**
     * 전송한 사용자의 닉네입
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @set:PropertyName("un")
    @get:PropertyName("un")
    var userNickname: String? = null,
    /**
     * 전송한 사용자의 프로필 url
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @set:PropertyName("url")
    @get:PropertyName("url")
    var profileUrl: String? = null,
    /**
     * 전송 TimeStamp 값
     * TimeStamp값은 UTC 기준입니다.
     */
    @set:PropertyName("t")
    @get:PropertyName("t")
    var insertTime: Long,
    /**
     * 한국 메세지
     */
    @set:PropertyName("mko")
    @get:PropertyName("mko")
    var koMessage: String,
    /**
     * 영문 메세지
     */
    @set:PropertyName("men")
    @get:PropertyName("men")
    var enMessage: String,
    /**
     * 메세지 locale 정보
     */
    @set:PropertyName("co")
    @get:PropertyName("co")
    var chattingLocale: String? = null,
    /**
     * 메세지 타입
     * [MessageType]을 참고하세요.
     */
    @set:PropertyName("mt")
    @get:PropertyName("mt")
    var messageType: String,
    /**
     * 전송한 사용자의 nft profile 유무 값
     */
    @set:PropertyName("nft")
    @get:PropertyName("nft")
    var isNFTProfile: Boolean = false,
    /**
     * 전송한 사용자의 AppName
     * [MessageType.TWITTER], [MessageType.BUY], [MessageType.SELL],
     * [MessageType.JUMP], [MessageType.DROP], [MessageType.MEDIUM]은 값이 null 입니다.
     */
    @set:PropertyName("ex")
    @get:PropertyName("ex")
    var appName: String? = null,
    /**
     * 답장할 메세지의 전송 시간 TimeStamp 값
     * TimeStamp값은 UTC 기준입니다.
     * 답장 메세지가 아닐경우 0 입니다.
     */
    @set:PropertyName("rp_mt")
    @get:PropertyName("rp_mt")
    var replyInsertTime: Int = 0,
    /**
     * 답장할 메세지의 id
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @set:PropertyName("rp_msg_id")
    @get:PropertyName("rp_msg_id")
    var replyMessageId: String? = null,
    /**
     * 답장할 메세지의 사용자 닉네임
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @set:PropertyName("rp_un")
    @get:PropertyName("rp_un")
    var replyUserNickName: String? = null,
    /**
     * 답장할 메세지의 국문 메세지
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @set:PropertyName("rp_mko")
    @get:PropertyName("rp_mko")
    var replyKoMessage: String? = null,
    /**
     * 답장할 메세지의 영문 메세지
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @set:PropertyName("rp_men")
    @get:PropertyName("rp_men")
    var replyEnMessage: String? = null,

    /**
     * 답장할 메세지의 사용자 id
     * 답장 메세지가 아닐경우 null 입니다.
     */
    @set:PropertyName("rp_mid")
    @get:PropertyName("rp_mid")
    var replyUserId: String? = null,
    /**
     * 메세지 라벨
     * AMA 진행 중 보내진 메세지라면 "AMA" 값이 전달 되고 그 이외일 경우 null 입니다.
     */
    @set:PropertyName("lab")
    @get:PropertyName("lab")
    var label: String? = null,
    /**
     * 메세지 이모지 리액션
     */
    @set:PropertyName("emo")
    @get:PropertyName("emo")
    var emoji: HashMap<String, Emoji>? = null,
    /**
     * 이미지 메세지의 url 리스트
     * 이미지 메세지가 아닐 경우 null 입니다.
     */
    @set:PropertyName("img")
    @get:PropertyName("img")
    var images: ArrayList<String>? = null,
    /**
     * 전송 사용자의 홀더 유무 값
     */
    @set:PropertyName("isHolder")
    @get:PropertyName("isHolder")
    var isHolder: Boolean = false,
    /**
     * 자산 인증
     * 메세지 타임이 [MessageType.ASSET]이 아닐 경우 null 입니다.
     */
    @set:PropertyName("asset")
    @get:PropertyName("asset")
    var asset: Asset? = null,
    /**
     * 서버 타임스템프 값
     * TimeStamp값은 UTC 기준입니다.
     */
    @set:PropertyName("st")
    @get:PropertyName("st")
    var st: Timestamp? = null,
): Parcelable {
    constructor() : this("", "", "", "", "", "", "", 0, "", "", "", "")
}
