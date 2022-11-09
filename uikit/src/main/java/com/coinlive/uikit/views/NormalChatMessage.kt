package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.ViewNormalChatItemBinding

class NormalChatMessage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewNormalChatItemBinding by lazy {
        ViewNormalChatItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.NormalChatMessage, 0, 0).apply {
            if (!isInEditMode) {
                val isMe = getBoolean(R.styleable.NormalChatMessage_isMe, false)
                binding.tvMsg.apply {
                    setTextColor(getColor(R.styleable.NormalChatMessage_textColor, ContextCompat.getColor(context, R
                        .color.chat_msg_text)))
                    background = if(isMe) {
                        ContextCompat.getDrawable(context,R.drawable.normal_me_msg_background)
                    } else {
                        ContextCompat.getDrawable(context,R.drawable.normal_other_msg_background)
                    }
                    text = getString(R.styleable.NormalChatMessage_text)
                }
            }
            recycle()
        }
    }

}