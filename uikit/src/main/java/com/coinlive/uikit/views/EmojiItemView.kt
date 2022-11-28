package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.coinlive.uikit.databinding.ViewEmojiItemBinding
import com.coinlive.uikit.models.EmojiItem


interface OnEmojiEventListener {
    fun addEmoji(key: String)
    fun deleteEmoji(key: String)
}

class EmojiItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var listener: OnEmojiEventListener? = null
    private val binding: ViewEmojiItemBinding by lazy {
        ViewEmojiItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        if (!isInEditMode) {
            binding.root.setOnClickListener {
                binding.emoji?.let { emoji->
                    if (emoji.isSelect) {
                        listener?.deleteEmoji(emoji.key)
                    } else {
                        listener?.addEmoji(emoji.key)
                    }
                }
            }
        }
    }

    fun setEmojiItem(emoji: EmojiItem) {
        binding.emoji = emoji
    }

    fun getEmojiItem(): EmojiItem? = binding.emoji

    fun setListener(listener: OnEmojiEventListener) {
        this.listener = listener
    }

}