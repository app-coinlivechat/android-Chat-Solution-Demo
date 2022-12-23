package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.coinlive.uikit.databinding.ViewChattingMenuBinding

interface ChattingMenuItemClickListener {
    fun onClickShare()
    fun onClickNoti()
    fun onClickProfile()
    fun onClickTrans()
}

class ChattingMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var listener: ChattingMenuItemClickListener? = null

    private val binding: ViewChattingMenuBinding by lazy {
        ViewChattingMenuBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        if (!isInEditMode) {
            binding.clShare.setOnClickListener {
                listener?.onClickShare()
            }
            binding.clNoti.setOnClickListener {
                listener?.onClickNoti()
            }
            binding.clProfile.setOnClickListener {
                listener?.onClickProfile()
            }
            binding.clTrans.setOnClickListener {
                listener?.onClickTrans()
            }
        }
    }

    fun setIsAnonymously(isAnonymously: Boolean) {
        binding.isAnonymously = isAnonymously
    }

    fun setListener(listener: ChattingMenuItemClickListener) {
        this.listener = listener
    }


}