package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.ViewInputBinding


class InputView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ViewInputBinding by lazy { ViewInputBinding.inflate(LayoutInflater.from(context), this, true) }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.InputView, 0, 0).apply {
            if (!isInEditMode) {
                binding.root.setBackgroundColor(getColor(R.styleable.InputView_viewBackground,
                    getColor(R.color.background)))
                binding.vDivider.apply {
                    setBackgroundColor(getColor(R.styleable.InputView_dividerColor, getColor(R.color.line_color)))
                }
                binding.ibtnAddOn.apply {
                    setImageResource(getResourceId(R.styleable.InputView_addOnImage, R.drawable.icon_additonal))
                }
                binding.ibtnSend.apply {
                    setImageResource(getResourceId(R.styleable.InputView_sendImage, R.drawable.icon_send))
                }
                binding.etInput.apply {
                    setHintTextColor(getColor(R.styleable.InputView_hitTextColor, getColor(R.color.grey_040)))
                    background = getDrawable(R.styleable.InputView_inputBackground)
                        ?: ContextCompat.getDrawable(context, R.drawable.shape_input_background)
                }
            }
            recycle()
        }
    }

    fun setAma(isAma:Boolean) {
        binding.isAMA = isAma
    }

    fun setLoginUser(isLoginUser:Boolean) {
        binding.isLoginUser = isLoginUser
    }

    fun setActiveUser(userStatus: UserStatus?) {
        userStatus?.let {
            binding.isActiveUser = it == UserStatus.ACTIVE
        } ?: run {
            binding.isActiveUser = false
        }


    }

    private fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }
}