package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.ViewNewMessageBinding

class NewMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ViewNewMessageBinding by lazy {
        ViewNewMessageBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        if (!isInEditMode) {
        }
    }

    fun setMyInfo(customerUser: CustomerUser?) {
        binding.myInfo = customerUser
        updateNewMessage()
    }

    fun setChat(chat: Chat?) {
        binding.chat = chat
        updateNewMessage()
    }

    private fun updateNewMessage() {
        binding.chat?.let { chat ->
            var message = (if (Coinlive.locale.language == "ko") chat.koMessage else chat.enMessage) ?: ""

            if (message.isEmpty()) {
                message = context.getString(R.string.new_message)
            }
            binding.myInfo?.let { myinfo ->
                val blockUser = myinfo.blockUserMidList.find { it == chat.memberId }
                if (blockUser != null) {
                    message = context.getString(R.string.blocked_user_message)
                }
            }
            binding.message = message
        }
    }
}