package com.coinlive.demo

import android.app.Application
import com.coinlive.chat.Coinlive
import java.util.*

class DemoApplication : Application() {


    companion object {
        val appName = "testsite"
        val password = "testsite"
    }

    override fun onCreate() {
        super.onCreate()

        // coinlive
        Coinlive.isDebug = BuildConfig.DEBUG
        Coinlive.locale = Locale.getDefault()
    }
}