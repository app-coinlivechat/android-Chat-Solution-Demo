package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

/**
 * Coinlive에서 발생한 자산 인증 메세지 모델 클래스
 */
data class Asset(
    /**
     * 자산 인증시 선택한 이모지 key 값
     */
    @SerializedName("selectedEmoji") val emoji:String,
    /**
     * 자산 인증 거래소 및 지갑 이름
     */
    val exchange:String,
    /**
     * 자산 인증 수량
     */
    val amount:Int,
    /**
     * 자산 인증 원화 환산 금액
     */
    val priceWon:Int,
    /**
     * 자산 인증 달러 환산 금액
     */
    val priceDol:Int,
    /**
     * 자산 인증 사용자 닉네임
     */
    val nickname:Int,
    /**
     * 자산 인증한 코인 id
     */
    val coinId:String,
    /**
     * 자산 인증한 코인 심볼
     */
    val symbol:String,
    /**
     * 자산 인증한 코인 심볼 url
     */
    val iconUrl:String
)
