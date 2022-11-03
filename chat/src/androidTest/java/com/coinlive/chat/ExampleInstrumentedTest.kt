package com.coinlive.chat

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.coinlive.chat", appContext.packageName)
    }

    @Test
    fun locale() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        FirebaseApp.getInstance()
        val locale = appContext.resources.configuration.locales[0]
        assertEquals("ko", locale.toString().substring(0,2))
    }
}