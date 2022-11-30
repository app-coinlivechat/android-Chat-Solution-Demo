package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.ViewDialogConfirmBinding

interface OnButtonEventListener {
    fun onConfirmClick()
    fun onCancelClick()
}

class DialogConfirmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ViewDialogConfirmBinding by lazy {
        ViewDialogConfirmBinding.inflate(LayoutInflater.from(context), this, true)
    }
    private var listener: OnButtonEventListener? = null

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ConfirmDialog, 0, 0).apply {
            if (!isInEditMode) {



                getDrawable(R.styleable.ConfirmDialog_confirmBackground)?.let {
//                    binding.btnConfirm.background = it
                    binding.btnConfirm.background = ContextCompat.getDrawable(context, R.drawable.select_dialog_confirm_red_background)
                }
//                getDrawable(R.styleable.ConfirmDialog_cancelBackground)?.let {
//                    binding.btnCancel.background = it
//                }
                binding.btnConfirm.setOnClickListener {
                    listener?.onConfirmClick()
                }
                binding.btnCancel.setOnClickListener {
                    listener?.onCancelClick()
                }
            }
            recycle()
        }

    }

    fun setDescription(description: String) {
        binding.description = description
    }

    fun setConfirmText(text: String) {
        binding.confirmText = text
    }

    fun setListener(listener: OnButtonEventListener) {
        this.listener = listener
    }

}