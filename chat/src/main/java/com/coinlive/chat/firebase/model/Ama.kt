package com.coinlive.chat.firebase.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

/**
 * AMA 진행 상태 모델 클래스
 */
@Parcelize
@Keep
data class Ama(
    /**
     * AMA 진행 중 채팅창 입력을 막을 경우 true 값이 됩니다.
     * [isPause] 값과 상관없이 Customer App에서는 AMA 진행 중 일 경우 채팅을 입력할 수 없습니다.
     */
    @set:PropertyName("pause")
    @get:PropertyName("pause")
    var isPause:Boolean = false,
    /**
     * AMA 시작 TimeStamp 값
     * TimeStamp값은 UTC 기준입니다.
     */
    @set:PropertyName("start")
    @get:PropertyName("start")
    var startTime:Long = 0,
    /**
     * AMA 종료 TimeStamp 값
     * [startTime]값은 존재하되 [endTime] 값이 null 인 경우 현재 AMA가 진행 중인걸 나타냅니다.
     * TimeStamp값은 UTC 기준입니다.
     */
    @set:PropertyName("end")
    @get:PropertyName("end")
    var endTime:Long?,
    /**
     * AMA 진행 중 사용자들이 하트 리액션을 누른 횟수 입니다.
     */
    @set:PropertyName("heart")
    @get:PropertyName("heart")
    var heartCount:Int = 0
): Parcelable {
    constructor() : this(false,0,0,0)
}
