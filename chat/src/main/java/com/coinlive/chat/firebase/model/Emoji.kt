package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

/**
 * 메세지의 이모지 리액션 모델 클래스
 */
data class Emoji(
    /**
     * 리액션 카운트
     */
    @SerializedName("cnt") val count:Int,
    /**
     * 이모지 리액션을 한 사용자의 id 리스트
     */
    @SerializedName("mid") val mIds:ArrayList<String>
)

