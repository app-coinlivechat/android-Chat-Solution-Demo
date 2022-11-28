package com.coinlive.uikit.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.coinlive.chat.firebase.model.Emoji
import com.coinlive.chat.firebase.model.enum.EmojiType
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.ViewEmojiBinding
import com.coinlive.uikit.models.EmojiItem

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var emojiMap: Map<String, Emoji>? = null
    private var mId: String? = null
    private val binding: ViewEmojiBinding by lazy {
        ViewEmojiBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        if (!isInEditMode) {

        }
    }


    private fun getEmojiResource(key: String): Drawable? {
        return when (key) {
            EmojiType.GOOD.key -> ContextCompat.getDrawable(binding.root.context, R.drawable.img_emoji_good)
            EmojiType.HEART.key -> ContextCompat.getDrawable(binding.root.context, R.drawable.img_emoji_heart)
            EmojiType.CLAP.key -> ContextCompat.getDrawable(binding.root.context, R.drawable.img_emoji_clap)
            EmojiType.ROCKET.key -> ContextCompat.getDrawable(binding.root.context, R.drawable.img_emoji_rocket)
            EmojiType.CRY.key -> ContextCompat.getDrawable(binding.root.context, R.drawable.img_emoji_cry)
            else -> ContextCompat.getDrawable(binding.root.context, R.drawable.img_emoji_astonished)
        }
    }


    fun setEmojiMap(emoji: HashMap<String, Emoji>?) {
        this.emojiMap = emoji
        binding.root.visibility = if (emojiMap == null) View.GONE else View.VISIBLE
        val items: ArrayList<EmojiItem> = arrayListOf()
        val viewItems: ArrayList<EmojiItemView> = arrayListOf(binding.emoji1, binding.emoji2, binding.emoji3,
            binding.emoji4, binding.emoji5, binding.emoji6)
        emojiMap?.forEach { (key, emoji) ->
            val emojiResource = getEmojiResource(key) ?: return@forEach
            val item = EmojiItem(resource = emojiResource, count = emoji.count, isSelect = if(mId == null) false
                    else emoji.mIds!!.contains(mId), key = key)
            items.add(item)
        }

        items.sortedBy { it.count }
        viewItems.forEachIndexed { index, view ->
            if (index > items.size - 1) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
                val emojiItem = items[index]
                view.setEmojiItem(emojiItem)
            }
        }

    }

    fun setMyMid(id: String?) {
        this.mId = id
    }

    fun setEmojiListener(listener: OnEmojiEventListener) {
        binding.emoji1.setListener(listener)
        binding.emoji2.setListener(listener)
        binding.emoji3.setListener(listener)
        binding.emoji4.setListener(listener)
        binding.emoji5.setListener(listener)
        binding.emoji6.setListener(listener)
    }


}