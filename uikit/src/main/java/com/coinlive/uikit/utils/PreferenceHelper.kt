package com.coinlive.uikit.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private val TRANSLATOR_LANGUAGE = "TRANSLATOR_LANGUAGE"
    private val TRANSLATOR_ENABLE = "TRANSLATOR_ENABLE"

    fun defaultPreference(context: Context): SharedPreferences = context.getSharedPreferences("coinlive", Context.MODE_PRIVATE)


    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.translatorLanguage
        get() = getString(TRANSLATOR_LANGUAGE,null)
        set(value) {
            editMe {
                it.putString(TRANSLATOR_LANGUAGE, value)
            }
        }

    var SharedPreferences.isTranslatorEnable
        get() = getBoolean(TRANSLATOR_ENABLE,false)
        set(value) {
            editMe {
                it.putBoolean(TRANSLATOR_ENABLE, value)
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