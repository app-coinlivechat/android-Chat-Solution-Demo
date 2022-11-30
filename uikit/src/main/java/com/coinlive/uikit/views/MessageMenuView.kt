package com.coinlive.uikit.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.EmojiType
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.PopupMessageMenuBinding

interface OnMessageMenuEventListener {
    fun onClickBlockMenu(chat: Chat, isReadyBlock: Boolean)
    fun onClickCopyMenu(chat: Chat)
    fun onClickDeleteMenu(chat: Chat)
    fun onClickReportMenu(chat: Chat)
    fun onClickCancelMenu()
    fun onDeleteEmoji(chat: Chat, key: String)
    fun onAddEmoji(chat: Chat, key: String)
}


class MessageMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val binding: PopupMessageMenuBinding by lazy {
        PopupMessageMenuBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var listener: OnMessageMenuEventListener? = null

    init {
        if (!isInEditMode) {
            binding.clBlock.setOnClickListener(this)
            binding.clCopy.setOnClickListener(this)
            binding.clDelete.setOnClickListener(this)
            binding.clReport.setOnClickListener(this)
            binding.ibtnCry.setOnClickListener(this)
            binding.ibtnGood.setOnClickListener(this)
            binding.ibtnClap.setOnClickListener(this)
            binding.ibtnHeart.setOnClickListener(this)
            binding.ibtnRocket.setOnClickListener(this)
            binding.ibtnAstonished.setOnClickListener(this)
        }
    }


    fun setChat(chat: Chat) {
        binding.chat = chat
        initEmoji()
    }

    fun setIsReadyBlock(isReadyBlock: Boolean) {
        binding.isReadyBlock = isReadyBlock
    }

    fun setMyMid(mId: String?) {
        binding.myMid = mId
    }


    fun setListener(listener: OnMessageMenuEventListener) {
        this.listener = listener
    }

    private fun initEmoji() {
        binding.ibtnGood.background = getBackgroundDrawable(EmojiType.GOOD.key)
        binding.ibtnCry.background = getBackgroundDrawable(EmojiType.CRY.key)
        binding.ibtnClap.background = getBackgroundDrawable(EmojiType.CLAP.key)
        binding.ibtnHeart.background = getBackgroundDrawable(EmojiType.HEART.key)
        binding.ibtnRocket.background = getBackgroundDrawable(EmojiType.ROCKET.key)
        binding.ibtnAstonished.background = getBackgroundDrawable(EmojiType.ASTONISHED.key)
    }

    private fun getBackgroundDrawable(key: String): Drawable? {
        if (binding.chat == null || binding.myMid == null) return ContextCompat.getDrawable(binding.root.context,
            R.color.transparent)
        if (binding.chat!!.emoji == null) return ContextCompat.getDrawable(binding.root.context, R.color.transparent)
        return if (binding.chat!!.emoji!![key]?.mIds?.contains(binding.myMid) == true)
            ContextCompat.getDrawable(binding.root.context, R.drawable.shape_message_emoji_menu_select_background)
        else
            ContextCompat.getDrawable(binding.root.context, R.color.transparent)
    }

    private fun callEmojiEvent(key: String) {
        if (binding.myMid == null) return
        binding.chat!!.emoji?.let {
            if (it[key]?.mIds?.contains(binding.myMid) == true)
                listener!!.onDeleteEmoji(binding.chat!!, key)
            else
                listener!!.onAddEmoji(binding.chat!!, key)
        } ?: run {
            listener!!.onAddEmoji(binding.chat!!, key)
        }
    }

    override fun onClick(v: View?) {
        if (listener == null || binding.chat == null) return
        when (v?.id) {
            binding.ibtnGood.id -> callEmojiEvent(EmojiType.GOOD.key)
            binding.ibtnCry.id -> callEmojiEvent(EmojiType.CRY.key)
            binding.ibtnClap.id -> callEmojiEvent(EmojiType.CLAP.key)
            binding.ibtnHeart.id -> callEmojiEvent(EmojiType.HEART.key)
            binding.ibtnRocket.id -> callEmojiEvent(EmojiType.ROCKET.key)
            binding.ibtnAstonished.id -> callEmojiEvent(EmojiType.ASTONISHED.key)
            binding.clBlock.id -> listener!!.onClickBlockMenu(binding.chat!!,binding.isReadyBlock!!)
            binding.clCopy.id -> listener!!.onClickCopyMenu(binding.chat!!)
            binding.clReport.id -> listener!!.onClickReportMenu(binding.chat!!)
            binding.clDelete.id -> listener!!.onClickDeleteMenu(binding.chat!!)
            binding.clCancel.id -> listener!!.onClickCancelMenu()
        }
    }


}