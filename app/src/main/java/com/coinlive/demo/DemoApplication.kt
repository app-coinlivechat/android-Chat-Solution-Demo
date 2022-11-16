package com.coinlive.demo

import android.app.Application
import com.coinlive.chat.Coinlive
import java.util.*

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // coinlive
        Coinlive.isDebug = BuildConfig.DEBUG
        Coinlive.locale = Locale.getDefault()
    }
}