package com.coinlive.chat.util

import java.util.*

object CalendarHelper {

    /**
     * UTC 기준 오늘 자정 타임스템프
     */
    fun getTodayMidnightTimeStamp(): Long {
        return getTodayMidnight().timeInMillis
    }

    /**
     * UTC 기준
     */
    fun getYesterdayMidnightTimeStamp(): Long {
        val today = getTodayMidnight();
        today.set(Calendar.DATE, today.get(Calendar.DATE) - 1)
        return today.timeInMillis
    }

    fun getDayAgoMidnightTimeStamp(daysAgo:Int) : Long {
        val today = getTodayMidnight();
        today.set(Calendar.DATE, today.get(Calendar.DATE) - daysAgo)
        return today.timeInMillis
    }

    private fun getTodayMidnight(): Calendar {
        val now = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val tempTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        tempTime.clear()
        tempTime.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0)
        return tempTime
    }

}