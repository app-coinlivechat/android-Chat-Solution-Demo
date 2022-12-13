package com.coinlive.uikit.viewholders

import androidx.constraintlayout.widget.ConstraintSet
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
    isMessageMenu : Boolean = false,
    private val eventListener: MessageEventListener? = null,
    private val itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {

    init {
        binding.isMessageMenu = isMessageMenu
        if(!isMessageMenu) {
            binding.clMaxMsg.setOnLongClickListener { onLonClick(it) }

            binding.tvMsg.setOnLongClickListener { onLonClick(it) }

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

    override fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean) {
        super.bind(item, isSameDate, isRoundMessage)
        binding.chat = item
        binding.isRoundMessage = isRoundMessage
        binding.isSameDate = isSameDate
        binding.message = if (Coinlive.locale.language.equals("ko")) item.koMessage else item.enMessage ?: ""

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.clRoot)
        if (binding.message!!.length > 400) {
            constraintSet.connect(binding.tvTime.id, ConstraintSet.END, binding.clMaxMsg.id, ConstraintSet.START)
            constraintSet.connect(binding.tvTime.id,
                ConstraintSet.BOTTOM,
                binding.clMaxMsg.id,
                ConstraintSet.BOTTOM)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.clMaxMsg.id, ConstraintSet.BOTTOM)
        } else {
            constraintSet.connect(binding.tvTime.id, ConstraintSet.END, binding.tvMsg.id, ConstraintSet.START)
            constraintSet.connect(binding.tvTime.id,
                ConstraintSet.BOTTOM,
                binding.clMaxMsg.id,
                ConstraintSet.BOTTOM)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.tvMsg.id, ConstraintSet.BOTTOM)

        }
        constraintSet.applyTo(binding.clRoot)
    }
}