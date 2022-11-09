package com.coinlive.chat.firebase.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 메세지의 이모지 리액션 모델 클래스
 */
@Parcelize
@Keep
data class Emoji(
    /**
     * 리액션 카운트
     */
    @set:PropertyName("cnt")
    @get:PropertyName("cnt")
    var count:Int,
    /**
     * 이모지 리액션을 한 사용자의 id 리스트
     */
    @set:PropertyName("mid")
    @get:PropertyName("mid")
    var mIds:ArrayList<String>? = null
): Parcelable {
    constructor() :this(0)
}

