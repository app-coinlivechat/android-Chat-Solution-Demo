package com.coinlive.chat

import com.coinlive.chat.util.CalendarHelper
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun timeStamp() {

//        assertEquals(1666224000000,CalendarHelper.getTodayMidnightTimeStamp())
//        assertEquals(1666137600000,CalendarHelper.getYesterdayMidnightTimeStamp())


        assertEquals(1667260800000, CalendarHelper.getMidnightTimeStampByMillis(1667298926000))
//        assertEquals(1667260800000, CalendarHelper.getTodayMidnightTimeStamp())

        Coinlive.isDebug = false
    }
}