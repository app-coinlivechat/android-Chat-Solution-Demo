package com.coinlive.uikit.viewholders

import com.coinlive.chat.Coinlive
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.databinding.ViewOtherAssetChatItemBinding
import com.coinlive.uikit.views.OnEmojiEventListener
import java.text.NumberFormat

class OtherAssetMessageViewHolder(
    private val binding: ViewOtherAssetChatItemBinding,
    isMessageMenu: Boolean = false,
    private val eventListener: MessageEventListener?,
    private val itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {

    init {
        binding.isMessageMenu = isMessageMenu
        if (!isMessageMenu) {
            binding.root.setOnLongClickListener {
                itemListener?.getItem(adapterPosition)?.let { chat ->
                    val isRoundMessage = binding.isRoundMessage ?: false
                    eventListener?.onLongClick(chat, binding.clAsset, itemViewType, isRoundMessage)
                }
                true
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
        binding.chat = item
        binding.locale = Coinlive.locale.language
        binding.isRoundMessage = isRoundMessage
        binding.isSameDate = isSameDate
        binding.isShowTime = isShowTime

        val price = if (binding.locale!! == "ko") item.asset!!.priceWon else item.asset!!.priceDol

        binding.price = "$${item.symbol} ${String.format("%.4f", item.asset!!.amount)} (${
            NumberFormat.getCurrencyInstance(Coinlive.locale).format(price)
        })"
        binding.base.setIsEnableTranslator(false)
        binding.emoji.setMyMid(itemListener?.getMyInfo()?.id)
    }
}