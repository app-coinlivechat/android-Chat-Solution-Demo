package com.coinlive.uikit.viewholders

import com.coinlive.chat.Coinlive
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.bindingadapterex.BindingAdapters
import com.coinlive.uikit.databinding.ViewServerChatItemBinding

class ServerViewHolder(
    private val binding: ViewServerChatItemBinding,
    private val coinName : String,
    eventListener: MessageEventListener? = null,
    itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {
    override fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean) {
        super.bind(item, isSameDate, isRoundMessage)
        binding.chat = item
        binding.locale = Coinlive.locale.language
        binding.coinName = coinName
        binding.isSameDate = isSameDate

        when (itemViewType) {
            0 -> {
                binding.ivCon.setImageResource(R.drawable.icon_binance)
                BindingAdapters.futureTitle(binding.tvTitle, item.messageType)
            }
            1 -> {
                binding.ivCon.setImageResource(R.drawable.icon_twitter)
                binding.tvTitle.text =
                    binding.tvTitle.context.getString(R.string.twitter_chat_title, coinName, item.symbol)
            }
            2 -> {
                binding.ivCon.setImageResource(R.drawable.icon_waring)
                BindingAdapters.priceTitle(binding.tvTitle, item.messageType)

            }
            else -> {
                binding.ivCon.setImageResource(R.drawable.icon_medium)
                binding.tvTitle.text =
                    binding.tvTitle.context.getString(R.string.medium_chat_title, coinName, item.symbol)
            }
        }
    }
}