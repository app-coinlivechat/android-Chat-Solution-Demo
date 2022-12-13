package com.coinlive.uikit.views

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.coinlive.uikit.R
import com.coinlive.uikit.utils.ViewUtils.dpToPx
import com.google.android.material.snackbar.Snackbar

class CoinLiveToast(view: View, message: String) {

    private val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

    companion object {
        fun make(view: View, message: String) = CoinLiveToast(view, message)
    }

    init {
        snackbar.view.apply {
            layoutParams = (layoutParams as CoordinatorLayout.LayoutParams).apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                setMargins(0,0,0,dpToPx(97F))
            }

            background = ContextCompat.getDrawable(view.context,R.drawable.shape_toast_background)
        }
    }

    fun show() {
        snackbar.dismiss()
        snackbar.show()
    }


}