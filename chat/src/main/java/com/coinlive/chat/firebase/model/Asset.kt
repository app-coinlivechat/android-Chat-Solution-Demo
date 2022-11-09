package com.coinlive.chat.firebase.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Coinlive에서 발생한 자산 인증 메세지 모델 클래스
 */
@Parcelize
@Keep
data class Asset(
    /**
     * 자산 인증시 선택한 이모지 key 값
     */
    @set:PropertyName("selectedEmoji")
    @get:PropertyName("selectedEmoji")
    var emoji: String? = null,
    /**
     * 자산 인증 거래소 및 지갑 이름
     */
    var exchange: String? = null,
    /**
     * 자산 인증 수량
     */
    var amount: Int? = null,
    /**
     * 자산 인증 원화 환산 금액
     */
    var priceWon: Int? = null,
    /**
     * 자산 인증 달러 환산 금액
     */
    var priceDol: Int? = null,
    /**
     * 자산 인증 사용자 닉네임
     */
    var nickname: Int? = null,
    /**
     * 자산 인증한 코인 id
     */
    var coinId: String? = null,
    /**
     * 자산 인증한 코인 심볼
     */
    var symbol: String? = null,
    /**
     * 자산 인증한 코인 심볼 url
     */
    var iconUrl: String? = null,
) : Parcelable {
    constructor() : this("")
}
