package com.coinlive.uikit.viewholders

import android.graphics.Rect
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.Coinlive
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.ImageMessageListAdapter
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.bindingadapterex.BindingAdapters
import com.coinlive.uikit.databinding.ViewOtherImageChatItemBinding
import com.coinlive.uikit.views.OnEmojiEventListener

class OtherImageMessageViewHolder(
    private val binding: ViewOtherImageChatItemBinding,
    isMessageMenu : Boolean = false,
    private val eventListener: MessageEventListener? = null,
    private val itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {
    init {
        binding.isMessageMenu = isMessageMenu
        if(!isMessageMenu) {
            binding.ivOne.setOnLongClickListener { onLonClick(it) }

            binding.ibtnProfile.setOnClickListener {
                itemListener?.getItem(adapterPosition)?.let { chat->
                    eventListener?.onProfileClick(chat, it)
                }
            }

            binding.emoji.setEmojiListener(object : OnEmojiEventListener {
                override fun addEmoji(key: String) {
                    itemListener?.getItem(adapterPosition)?.let { chat->
                        eventListener?.addEmoji(chat, key)
                    }
                }

                override fun deleteEmoji(key: String) {
                    itemListener?.getItem(adapterPosition)?.let { chat->
                        eventListener?.deleteEmoji(chat, key)
                    }
                }
            })
        }

    }

    private fun itemImageLonClick() {
        onLonClick(binding.rvList)
    }

    override fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean) {
        super.bind(item, isSameDate, isRoundMessage)
        val myInfo = itemListener?.getMyInfo()

        binding.chat = item
        binding.locale = Coinlive.locale.language
        binding.isRoundMessage = isRoundMessage
        binding.isSameDate = isSameDate
        binding.isBlockUser = myInfo?.blockUserMidList?.contains(item.memberId) ?: false

        binding.emoji.setMyMid(myInfo?.id)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.clRoot)

        if (binding.isBlockUser!!) {
            binding.ivOne.setImageResource(R.drawable.img_block_image)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.ivOne.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.ivOne.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.ivOne.id, ConstraintSet.BOTTOM)
            constraintSet.applyTo(binding.clRoot)
            return
        }

        if (item.images!!.size > 1) {
            LoggerHelper.d("no block user image!!")

            val layoutManager = GridLayoutManager(binding.rvList.context, if (item.images!!.size > 6) 3 else 2)
            binding.rvList.apply {
                this.layoutManager = layoutManager
                this.setPadding(0)
                this.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State,
                    ) {
                        outRect.set(0, 0, 0, 0)
                    }
                })
                this.adapter = ImageMessageListAdapter(item.images!!) { itemImageLonClick() }

            }

            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.rvList.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.rvList.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.rvList.id, ConstraintSet.BOTTOM)
        } else {
            BindingAdapters.loadImageMessage(binding.ivOne, item.images!![0])
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.ivOne.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.ivOne.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.ivOne.id, ConstraintSet.BOTTOM)
        }
        constraintSet.applyTo(binding.clRoot)
    }
}