package com.coinlive.uikit.viewholders

import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.coinlive.chat.Coinlive
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.bindingadapterex.BindingAdapters.loadImageMessage
import com.coinlive.uikit.databinding.ViewOtherImageChatItemBinding
import com.coinlive.uikit.utils.ViewUtils.dpToPx
import com.coinlive.uikit.views.OnEmojiEventListener


class OtherImageMessageViewHolder(
    private val binding: ViewOtherImageChatItemBinding,
    isMessageMenu: Boolean = false,
    private val eventListener: MessageEventListener?,
    private val itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {

    init {
        binding.isMessageMenu = isMessageMenu
        if (!isMessageMenu) {

            binding.glList.setOnLongClickListener {
                onLonClick(it, binding.isRoundMessage)
            }

            binding.ivOne.setOnLongClickListener { onLonClick(it, binding.isRoundMessage) }

            binding.ibtnProfile.setOnClickListener {
                itemListener?.getItem(adapterPosition)?.let { chat ->
                    eventListener?.onProfileClick(chat, it)
                }
            }

            binding.emoji.setEmojiListener(object : OnEmojiEventListener {
                override fun addEmoji(key: String) {
                    itemListener?.getItem(adapterPosition)?.let { chat ->
                        eventListener?.addEmoji(chat, key)
                    }
                }

                override fun deleteEmoji(key: String) {
                    itemListener?.getItem(adapterPosition)?.let { chat ->
                        eventListener?.deleteEmoji(chat, key)
                    }
                }
            })
        }
    }

    override fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean, isShowTime: Boolean) {
        super.bind(item, isSameDate, isRoundMessage, isShowTime)
        val myInfo = itemListener?.getMyInfo()

        binding.chat = item
        binding.locale = Coinlive.locale.language
        binding.isRoundMessage = isRoundMessage
        binding.isSameDate = isSameDate
        binding.isShowTime = isShowTime
        binding.isBlockUser = myInfo?.blockUserMidList?.contains(item.memberId) ?: false
        binding.emoji.setMyMid(myInfo?.id)

        if (binding.isBlockUser!!) {
            binding.ivOne.setImageResource(R.drawable.img_block_image)
            return
        }

        val imageSize = item.images!!.size

        if (imageSize > 1) {
            binding.glList.removeAllViews()
            val columnCount = if (imageSize > 4) 3 else 2
            val rowCount = (imageSize / if (imageSize > 4) 3 else 2) + 1
            LoggerHelper.de("columnCount: $columnCount, rowCount : $rowCount")

            val size = binding.glList.dpToPx(if (imageSize > 4) 75F else 100F)

            val lastIndex = imageSize - 1

            var i = 0
            var c = 0
            var r = 0
            while (i < imageSize) {
                if (c == columnCount) {
                    c = 0
                    r++
                }

                val imageView = ImageView(binding.glList.context)
                LoggerHelper.e("i : $i ,c : $c , r : $r")


                imageView.layoutParams = GridLayout.LayoutParams().apply {
                    this.width = size
                    this.height = size
                    if(rowCount - 1 != r) {
                        this.bottomMargin = imageView.dpToPx(3F)
                    }

                    if(c != columnCount - 1) {
                        this.rightMargin = imageView.dpToPx(3F)
                    } else {
                        this.width = size - (imageView.dpToPx(3F) * c)
                    }


                    this.columnSpec = GridLayout.spec(c)
                    this.rowSpec = GridLayout.spec(r)
                    if ((imageSize % 2) > 0) {  // 홀수

                        if (imageSize < 4) {
                            if (i == lastIndex) {
                                this.columnSpec = GridLayout.spec(c,2, GridLayout.FILL)
                                this.width = ViewGroup.LayoutParams.MATCH_PARENT
                                this.rightMargin = 0
                            }
//                        } else {
//                            if (i == lastIndex || i == (lastIndex - 1)) {
//                                if (imageSize - i <= 2) {
//                                    this.columnSpec = GridLayout.spec(c,2, GridLayout.FILL,1.5F)
//                                    this.width = ViewGroup.LayoutParams.MATCH_PARENT
//                                }
//                            }
                        }

                    } else if (i == 9) {  //item index 9 (총 10개)
                        this.columnSpec = GridLayout.spec(c,3, GridLayout.FILL)
                        this.width = ViewGroup.LayoutParams.MATCH_PARENT
                        this.rightMargin = 0
                    }
                }
                Glide.with(imageView.context)
                    .load(item.images!![i])
                    .transform(CenterCrop(), RoundedCorners(imageView.dpToPx(6F)))
                    .error(R.drawable.img_error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView)

                binding.glList.addView(imageView)
                i++
                c++
            }

        } else {
            loadImageMessage(binding.ivOne, item.images!![0])
        }
    }
}