package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.uikit.databinding.ViewOtherTextMessageBinding

class OtherTextMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewOtherTextMessageBinding by lazy {
        ViewOtherTextMessageBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setChat(item: Chat) {
        binding.chat = item
    }

    fun setIsRoundMessage(isRound: Boolean) {
        binding.isRoundMessage = isRound
    }

    fun setOriginMsg(message: String) {
        binding.originMsg = message
    }

    fun setIsSameDate(isSameDate: Boolean) {
        binding.isSameDate = isSameDate
    }

    fun setIsEnableTranslator(enableTranslator: Boolean) {
        binding.enableTranslator = enableTranslator
    }


}