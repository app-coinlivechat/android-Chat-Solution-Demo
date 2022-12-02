package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.ViewInputBinding


interface OnInputViewListener {
    fun sendMessage(text: String)
    fun onClickCamera()
    fun onClickGallery()
}


class InputView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ViewInputBinding by lazy { ViewInputBinding.inflate(LayoutInflater.from(context), this, true) }
    private var listener: OnInputViewListener? = null

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.InputView, 0, 0).apply {
            if (!isInEditMode) {
                binding.isClickAdd = false
                binding.root.setBackgroundColor(getColor(R.styleable.InputView_viewBackground,
                    getColor(R.color.background)))
                binding.vDivider.apply {
                    setBackgroundColor(getColor(R.styleable.InputView_dividerColor, getColor(R.color.line_color)))
                }
                binding.ibtnAddOn.apply {
                    setImageResource(getResourceId(R.styleable.InputView_addOnImage, R.drawable.icon_additonal))
                    setOnClickListener {
                        binding.isClickAdd = !binding.isClickAdd!!
                    }
                }
                binding.ibtnSend.apply {
                    setImageResource(getResourceId(R.styleable.InputView_sendImage, R.drawable.icon_send))
                    setOnClickListener {
                        val text = binding.etInput.text.toString()
                        if(text.isEmpty()) return@setOnClickListener
                        listener?.sendMessage(binding.etInput.text.toString())
                        binding.etInput.text.clear()
                    }
                }
                binding.etInput.apply {
                    setHintTextColor(getColor(R.styleable.InputView_hitTextColor, getColor(R.color.grey_040)))
                    background = getDrawable(R.styleable.InputView_inputBackground)
                        ?: ContextCompat.getDrawable(context, R.drawable.shape_input_background)
                    setOnFocusChangeListener { v, hasFocus ->
                        binding.ibtnAddOn.visibility = if (hasFocus) View.GONE else View.VISIBLE
                    }
                }

                binding.llCamera.setOnClickListener {
                    binding.isClickAdd = false
                    listener?.onClickCamera()
                }
                binding.llGallery.setOnClickListener {
                    binding.isClickAdd = false
                    listener?.onClickGallery()
                }
            }


            recycle()
        }
    }

    fun setIsAma(isAma: Boolean) {
        binding.isAMA = isAma
    }

    fun setLoginUser(isLoginUser: Boolean) {
        binding.isLoginUser = isLoginUser
    }

    fun setActiveUser(userStatus: UserStatus?) {
        userStatus?.let {
            binding.isActiveUser = it == UserStatus.ACTIVE
        } ?: run {
            binding.isActiveUser = false
        }
    }

    fun setSendMessageListener(listener: OnInputViewListener) {
        this.listener = listener
    }

    fun clearFocusEditeText() {
        binding.etInput.clearFocus()
    }

    private fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }
}