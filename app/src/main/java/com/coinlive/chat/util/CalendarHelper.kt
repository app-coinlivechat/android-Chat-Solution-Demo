package com.coinlive.chat.util

import java.util.*

object CalendarHelper {

    /**
     * UTC 기준 오늘 자정 타임스템프를 전달합니다.
     * @return[Long] 타임스탬프
     */
    fun getTodayMidnightTimeStamp(): Long {
        return getTodayMidnight().timeInMillis
    }

    /**
     * UTC 기준 어제 자정 타임스탬프를 전달합니다.
     * @return[Long] 타임스탬프
     */
    fun getYesterdayMidnightTimeStamp(): Long {
        val today = getTodayMidnight()
        today.set(Calendar.DATE, today.get(Calendar.DATE) - 1)
        return today.timeInMillis
    }

    fun getDayAgoMidnightTimeStamp(standardCalendar: Calendar,daysAgo:Int) : Long {
        standardCalendar.set(Calendar.DATE, standardCalendar.get(Calendar.DATE) - daysAgo)
        return standardCalendar.timeInMillis
    }

    /**
     * UTC 기준 오늘 자정 [Calendar] 를 전달합니다.
     */
    fun getTodayMidnight(): Calendar {
        val now = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val tempTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        tempTime.clear()
        tempTime.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0)
        return tempTime
    }

    /**
     * UTC 기준 현재 시간 [Calendar] 를 전달합니다.
     */
    fun nowCalendar(): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    }

}