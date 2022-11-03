package com.coinlive.chat.util

import android.util.Log
import com.coinlive.chat.BuildConfig

object LoggerHelper {
    const val TAG = "COINLIVE-CHAT"
    fun d(msg:String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG,msg)
        }
    }

    fun i(msg:String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG,msg)
        }
    }

    fun w(msg:String) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG,msg)
        }
    }

    fun e(msg:String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG,msg)
        }
    }

    fun dd(msg:String) {
        Log.d(TAG,msg)
    }

    fun di(msg:String) {
        Log.i(TAG,msg)
    }

    fun de(msg:String) {
        Log.e(TAG,msg)
    }

    fun dw(msg:String) {
        Log.w(TAG,msg)
    }

}