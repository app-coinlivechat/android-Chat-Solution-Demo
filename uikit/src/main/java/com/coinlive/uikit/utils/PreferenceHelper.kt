package com.coinlive.uikit.utils

import android.content.Context
import android.content.SharedPreferences
import com.coinlive.uikit.models.NoticeStatus

object PreferenceHelper {
    private val TRANSLATOR_LANGUAGE = "TRANSLATOR_LANGUAGE"
    private val TRANSLATOR_ENABLE = "TRANSLATOR_ENABLE"
    private val CM_STATUS = "_CM_STATUS"

    fun defaultPreference(context: Context): SharedPreferences =
        context.getSharedPreferences("coinlive", Context.MODE_PRIVATE)


    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.translatorLanguage
        get() = getString(TRANSLATOR_LANGUAGE, null)
        set(value) {
            editMe {
                it.putString(TRANSLATOR_LANGUAGE, value)
            }
        }

    var SharedPreferences.enableTranslator
        get() = getBoolean(TRANSLATOR_ENABLE, false)
        set(value) {
            editMe {
                it.putBoolean(TRANSLATOR_ENABLE, value)
            }
        }

    fun SharedPreferences.setCmStatus(cId: String, status: NoticeStatus) {
        editMe {
            it.putString("$cId$CM_STATUS", status.name)
        }
    }

    fun SharedPreferences.getCmStatus(cId: String): NoticeStatus? {
        return when (getString("$cId$CM_STATUS", null)) {
            NoticeStatus.NONE.name -> NoticeStatus.NONE
            NoticeStatus.SMALL.name -> NoticeStatus.SMALL
            NoticeStatus.FLOAT.name -> NoticeStatus.FLOAT
            NoticeStatus.BIG.name -> NoticeStatus.BIG
            else -> null
        }
    }

    var SharedPreferences.clearValues
        get() = run { }
        set(value) {
            editMe {
                it.clear()
            }
        }
}