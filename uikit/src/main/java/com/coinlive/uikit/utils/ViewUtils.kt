package com.coinlive.uikit.utils

import android.content.Context
import android.graphics.Rect
import android.util.Size
import android.util.TypedValue
import android.view.*


object ViewUtils {
    fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
        layoutParams<ViewGroup.MarginLayoutParams> {
            left?.run { leftMargin = dpToPx(this) }
            top?.run { topMargin = dpToPx(this) }
            right?.run { rightMargin = dpToPx(this) }
            bottom?.run { bottomMargin = dpToPx(this) }
        }
    }

    inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
    }

    fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

    fun Context.screenSize() : Size {
        val metrics = this.resources.displayMetrics
        return Size(metrics.widthPixels,metrics.heightPixels)
    }
    fun View.rect() : Rect {
        val screen = IntArray(2)
        this.getLocationOnScreen(screen)
        return Rect(screen[0],screen[1], screen[0] + this.width , screen[1] + this.height )
    }
}