package com.coinlive.uikit.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.EmojiType
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageListAdapter
import com.coinlive.uikit.databinding.PopupMessageMenuBinding
import com.coinlive.uikit.databinding.ViewMyTextMessageBinding
import com.coinlive.uikit.databinding.ViewOtherAssetChatItemBinding
import com.coinlive.uikit.databinding.ViewOtherImageChatItemBinding
import com.coinlive.uikit.databinding.ViewOtherTextMessageBinding
import com.coinlive.uikit.utils.ViewUtils.dpToPx
import com.coinlive.uikit.utils.ViewUtils.margin
import com.coinlive.uikit.utils.ViewUtils.rect
import com.coinlive.uikit.utils.ViewUtils.screenSize
import com.coinlive.uikit.viewholders.MyTextMessageViewHolder
import com.coinlive.uikit.viewholders.OtherAssetMessageViewHolder
import com.coinlive.uikit.viewholders.OtherImageMessageViewHolder
import com.coinlive.uikit.viewholders.OtherTextMessageViewHolder

interface OnMessageMenuEventListener {
    fun onClickBlockMenu(chat: Chat, isReadyBlock: Boolean)
    fun onClickCopyMenu(chat: Chat)
    fun onClickDeleteMenu(chat: Chat)
    fun onClickReportMenu(chat: Chat)
    fun onClickCancelMenu()
    fun onDeleteEmoji(chat: Chat, key: String)
    fun onAddEmoji(chat: Chat, key: String)
    fun onClickOutSide()
}

enum class MenuPosition {
    BOTTOM,
    TOP,
    OVERLAY
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
            binding.clContent.setOnClickListener(this)
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
                listener?.onDeleteEmoji(binding.chat!!, key)
            else
                listener?.onAddEmoji(binding.chat!!, key)
        } ?: run {
            listener?.onAddEmoji(binding.chat!!, key)
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
            binding.clBlock.id -> listener?.onClickBlockMenu(binding.chat!!, binding.isReadyBlock!!)
            binding.clCopy.id -> listener?.onClickCopyMenu(binding.chat!!)
            binding.clReport.id -> listener?.onClickReportMenu(binding.chat!!)
            binding.clDelete.id -> listener?.onClickDeleteMenu(binding.chat!!)
            binding.clCancel.id -> listener?.onClickCancelMenu()
            binding.clContent.id -> {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.clContent)
                constraintSet.connect(binding.llMessage.id, ConstraintSet.END, -1, ConstraintSet.END)
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.END, -1, ConstraintSet.END)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.END, -1, ConstraintSet.END)
                constraintSet.connect(binding.llMessage.id,
                    ConstraintSet.START,
                    binding.clContent.id,
                    ConstraintSet.START)
                constraintSet.connect(binding.llEmoji.id,
                    ConstraintSet.START,
                    binding.llMessage.id,
                    ConstraintSet.START)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.START, binding.llEmoji.id, ConstraintSet.START)

                constraintSet.connect(binding.llEmoji.id, ConstraintSet.TOP, binding.llMessage.id, ConstraintSet.BOTTOM)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.TOP, binding.llEmoji.id, ConstraintSet.BOTTOM)
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.BOTTOM, -1, ConstraintSet.TOP)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.BOTTOM, -1, ConstraintSet.TOP)


                constraintSet.applyTo(binding.clContent)
                listener?.onClickOutSide()
            }
        }
    }

    private fun getMenuPosition(view: View): MenuPosition {

        val size = binding.root.context.screenSize()
        val viewRect = view.rect()
        LoggerHelper.de("view.rect(): ${view.rect().flattenToString()}")

        if (viewRect.top < 219) {
            return MenuPosition.OVERLAY
        } else if (viewRect.height() > (size.height / 2)) {
            return MenuPosition.OVERLAY
        } else {
            if (viewRect.top > (size.height / 2)) {
                return MenuPosition.TOP
            }
        }

        return MenuPosition.BOTTOM
    }

    fun addMessage(view: View, viewType: Int, item: Chat, adapter: MessageListAdapter, isRoundMessage: Boolean) {

        binding.llMessage.removeAllViews()
        binding.llMessage.visibility = View.VISIBLE
        binding.llMessage.margin(top = 0F, right = 0F, bottom = 0F, left = 0F)

        val screen = IntArray(2)
        view.getLocationOnScreen(screen)
        val position = getMenuPosition(view)
        LoggerHelper.de("position: $position")

        when (viewType) {
            4 -> {
                val messageBinding = ViewOtherTextMessageBinding.inflate(LayoutInflater.from(view.context), null,
                    false)
                val holder = OtherTextMessageViewHolder(messageBinding, true, null, adapter)
                holder.bind(item, isSameDate = true, isRoundMessage = isRoundMessage, isShowTime = false)
                messageBinding.rlMsg.apply {
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = 0
                        marginStart = 0
                    }
                }
                binding.llMessage.apply {
                    addView(holder.itemView)
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = (screen[1] - dpToPx(30F))
                    }
                }
            }
            5 -> {
                val messageBinding = ViewOtherAssetChatItemBinding.inflate(LayoutInflater.from(view.context), null,
                    false)
                val holder = OtherAssetMessageViewHolder(messageBinding, true, null, adapter)
                holder.bind(item, isSameDate = true, isRoundMessage = isRoundMessage, isShowTime = false)
                messageBinding.base.getTvMsg().apply {
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = 0
                        marginStart = 0
                        bottomMargin = dpToPx(5F)
                    }
                }
                messageBinding.clAsset.apply {
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        marginStart = 0
                    }
                }

                binding.llMessage.apply {
                    addView(holder.itemView)
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = (screen[1] - dpToPx(72F))
                    }
                }
            }
            6 -> {
                val messageBinding = ViewOtherImageChatItemBinding.inflate(LayoutInflater.from(view.context), null,
                    false)
                val holder = OtherImageMessageViewHolder(messageBinding, true, null, adapter)
                holder.bind(item, isSameDate = true, isRoundMessage = isRoundMessage, isShowTime = false)
                messageBinding.rlImage.apply {
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = 0
                        marginStart = 0
                    }
                }
                binding.llMessage.apply {
                    addView(holder.itemView)
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = (screen[1] - dpToPx(30F))
                    }
                }
            }
            7 -> {
                val messageBinding = ViewMyTextMessageBinding.inflate(LayoutInflater.from(view.context), null,
                    false)
                val holder = MyTextMessageViewHolder(messageBinding, true, null, null)
                holder.bind(item, isSameDate = true, isRoundMessage = isRoundMessage, isShowTime = false)
                messageBinding.tvMsg.apply {
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = 0
                        marginStart = 0
                    }
                }
                messageBinding.clMaxMsg.apply {
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = 0
                        marginStart = 0
                    }
                }

                binding.llMessage.apply {
                    addView(holder.itemView)
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = (screen[1] - dpToPx(30F))
                    }
                }

                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.clContent)
                constraintSet.connect(binding.llMessage.id, ConstraintSet.END, binding.clContent.id, ConstraintSet.END)
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.END, binding.llMessage.id, ConstraintSet.END)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.END, binding.llEmoji.id, ConstraintSet.END)
                constraintSet.connect(binding.llMessage.id, ConstraintSet.START, -1, ConstraintSet.START)
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.START, -1, ConstraintSet.START)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.START, -1, ConstraintSet.START)
                constraintSet.applyTo(binding.clContent)
            }
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.clContent)
        when (position) {
            MenuPosition.TOP -> {
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.BOTTOM, binding.llMessage.id, ConstraintSet.TOP)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.BOTTOM, binding.llEmoji.id, ConstraintSet.TOP)
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.TOP, -1, ConstraintSet.BOTTOM)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.TOP, -1, ConstraintSet.BOTTOM)
            }
            MenuPosition.BOTTOM -> {
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.TOP, binding.llMessage.id, ConstraintSet.BOTTOM)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.TOP, binding.llEmoji.id, ConstraintSet.BOTTOM)
                constraintSet.connect(binding.llEmoji.id, ConstraintSet.BOTTOM, -1, ConstraintSet.TOP)
                constraintSet.connect(binding.clMenu.id, ConstraintSet.BOTTOM, -1, ConstraintSet.TOP)
            }
            MenuPosition.OVERLAY -> {
                binding.llMessage.removeAllViews()
                binding.llMessage.apply {
                    layoutParams = (layoutParams as MarginLayoutParams).apply {
                        topMargin = (screen[1] / 2)
                    }
                }
            }
        }
        constraintSet.applyTo(binding.clContent)

    }


}