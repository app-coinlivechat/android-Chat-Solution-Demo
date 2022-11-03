package com.coinlive.chat.firebase.model

import com.google.gson.annotations.SerializedName

/**
 * AMA 진행 상태 모델 클래스
 */
data class Ama(
    /**
     * AMA 진행 중 채팅창 입력을 막을 경우 true 값이 됩니다.
     * [isPause] 값과 상관없이 Customer App에서는 AMA 진행 중 일 경우 채팅을 입력할 수 없습니다.
     */
    @SerializedName("pause") val isPause:Boolean,
    /**
     * AMA 시작 TimeStamp 값
     * TimeStamp값은 UTC 기준입니다.
     */
    @SerializedName("start") val startTime:Long,
    /**
     * AMA 종료 TimeStamp 값
     * [startTime]값은 존재하되 [endTime] 값이 null 인 경우 현재 AMA가 진행 중인걸 나타냅니다.
     * TimeStamp값은 UTC 기준입니다.
     */
    @SerializedName("end") val endTime:Long?,
    /**
     * AMA 진행 중 사용자들이 하트 리액션을 누른 횟수 입니다.
     */
    @SerializedName("heart") val heartCount:Int
)
