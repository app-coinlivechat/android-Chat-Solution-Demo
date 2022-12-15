package com.coinlive.uikit.viewholders

import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.coinlive.chat.Coinlive
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.databinding.ViewMyTextMessageBinding
import com.coinlive.uikit.utils.Constants

class MyTextMessageViewHolder(
    private val binding: ViewMyTextMessageBinding,
    isMessageMenu: Boolean = false,
    private val eventListener: MessageEventListener?,
    private val itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {

    init {
        binding.isMessageMenu = isMessageMenu
        if (!isMessageMenu) {
            binding.clMaxMsg.setOnLongClickListener { onLonClick(it, binding.isRoundMessage) }

            binding.tvMsg.setOnLongClickListener { onLonClick(it, binding.isRoundMessage) }

            binding.clMaxMsg.setOnClickListener {
                binding.root.findNavController().navigate(R.id.action_chatFragment_to_textFragment,
                    bundleOf(Constants.argKeyTitle to binding.root.context.getString(R.string.read_all),
                        Constants.argKeyDescription to binding.message!!,
                        Constants.argKeyIsMyMessage to true,
                        Constants.argKeyAutoTranslator to false))
            }
            binding.ibtnDelete.setOnClickListener {
                LoggerHelper.d("onClickDelete position : $adapterPosition")
                itemListener?.getItem(adapterPosition)?.let { chat ->
                    eventListener?.onClickDelete(chat, it)
                }
            }
            binding.ibtnRetry.setOnClickListener {
                itemListener?.getItem(adapterPosition)?.let { chat ->
                    eventListener?.onClickRetry(chat, it)
                }
            }
        }

    }

    override fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean, isShowTime: Boolean) {
        super.bind(item, isSameDate, isRoundMessage, isShowTime)
        binding.chat = item
        binding.isRoundMessage = isRoundMessage
        binding.isSameDate = isSameDate
        binding.isShowTime = isShowTime
        binding.message = if (Coinlive.locale.language.equals("ko")) item.koMessage else item.enMessage ?: ""
    }
}