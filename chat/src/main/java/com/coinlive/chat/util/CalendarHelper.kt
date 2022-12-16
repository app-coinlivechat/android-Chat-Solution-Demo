package com.coinlive.chat.util

import androidx.annotation.Keep
import java.util.*

@Keep
object CalendarHelper {

    /**
     * UTC 기준 오늘 자정 타임스템프를 전달합니다.
     * @return[Long] 타임스탬프
     */
    fun getTodayMidnightTimeStamp(): Long {
        return getTodayMidnight().timeInMillis
    }

    /**
     * UTC 기준 오늘 자정 [Calendar] 를 전달합니다.
     */
    fun getTodayMidnight(): Calendar {
        val now = nowCalendar()
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0)
        now.set(Calendar.MILLISECOND,0)
        return now
    }

    fun getMidnightTimeStampByMillis(millis : Long) : Long {
        val empty = nowCalendar()
        empty.timeInMillis = millis
        empty.set(empty.get(Calendar.YEAR), empty.get(Calendar.MONTH), empty.get(Calendar.DATE), 0, 0, 0)
        empty.set(Calendar.MILLISECOND,0)
        return empty.timeInMillis
    }

    fun getMidnightCalendarByMillis(millis : Long) : Calendar {
        val empty = nowCalendar()
        empty.timeInMillis = millis
        empty.set(empty.get(Calendar.YEAR), empty.get(Calendar.MONTH), empty.get(Calendar.DATE), 0, 0, 0)
        empty.set(Calendar.MILLISECOND,0)
        return empty
    }

    /**
     * UTC 기준 현재 시간 [Calendar] 를 전달합니다.
     */
    fun nowCalendar(): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    }

    fun getCalendar(millis : Long) : Calendar {
        val calendar = nowCalendar()
        calendar.timeInMillis = millis
        return calendar
    }

}