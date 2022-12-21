package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.coinlive.uikit.databinding.ViewProgressBinding

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ViewProgressBinding by lazy {
        ViewProgressBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        if (!isInEditMode) {
            binding.clRoot.setOnClickListener {  }
        }
    }

}