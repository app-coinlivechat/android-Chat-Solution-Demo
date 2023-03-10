package com.coinlive.demo.utils

import android.content.Context
import android.util.TypedValue

object DpHelper {

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }
}