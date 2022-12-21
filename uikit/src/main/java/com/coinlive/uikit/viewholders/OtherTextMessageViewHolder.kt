package com.coinlive.uikit.viewholders

import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.databinding.ViewOtherTextMessageBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.PreferenceHelper
import com.coinlive.uikit.utils.PreferenceHelper.enableTranslator
import com.coinlive.uikit.utils.PreferenceHelper.translatorLanguage
import com.coinlive.uikit.views.OnEmojiEventListener
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class OtherTextMessageViewHolder(
    private val binding: ViewOtherTextMessageBinding,
    isMessageMenu : Boolean = false,
    private val eventListener: MessageEventListener?,
    private val itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {
    init {
        binding.isMessageMenu = isMessageMenu
        if(!isMessageMenu) {
            itemListener?.let {
                initClickListener()
            }
        }
    }

    private fun initClickListener() {
        binding.clMaxMsg.setOnLongClickListener { onLonClick(it,binding.isRoundMessage) }

        binding.clTrans.setOnLongClickListener { onLonClick(it,binding.isRoundMessage) }

        binding.tvMsg.setOnLongClickListener { onLonClick(it,binding.isRoundMessage) }

        binding.clMaxMsg.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_textFragment, bundleOf(Constants
                .argKeyTitle to it.context.getString(R.string.read_all),
                Constants.argKeyDescription to binding.originMsg))
        }
        binding.ibtnProfile.setOnClickListener {
            itemListener!!.getItem(adapterPosition)?.let { chat->
                eventListener?.onProfileClick(chat, it)
            }
        }

        binding.emoji.setEmojiListener(object : OnEmojiEventListener {
            override fun addEmoji(key: String) {
                itemListener!!.getItem(adapterPosition)?.let { chat->
                    eventListener?.addEmoji(chat, key)
                }
            }

            override fun deleteEmoji(key: String) {
                itemListener!!.getItem(adapterPosition)?.let { chat->
                    eventListener?.deleteEmoji(chat, key)
                }
            }
        })

        binding.ibtnTranslator.setOnClickListener {
            if (binding.originMsg!!.length > 400) {
                moveTextFragment(binding.originMsg!!)
            } else {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(Coinlive.locale.language)
                    .setTargetLanguage(PreferenceHelper.defaultPreference(it.context).translatorLanguage!!)
                    .build()
                val translator = Translation.getClient(options)
                translator.translate(binding.originMsg!!).addOnSuccessListener { transMsg ->
                    itemListener!!.getItem(adapterPosition)?.let { chat->
                        itemListener.setTranslatorItem(adapterPosition,chat.messageId,transMsg)
                    }

                    translator.close()
                }
            }
        }
    }

    override fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean, isShowTime: Boolean) {
        super.bind(item, isSameDate, isRoundMessage,isShowTime)

        val transMsg = itemListener?.getTranslatorItem(item.messageId)
        val message = if (Coinlive.locale.language.equals("ko")) item.koMessage else item.enMessage ?: ""
        val myInfo : CustomerUser? = itemListener?.getMyInfo()
        binding.chat = item
        binding.isRoundMessage = isRoundMessage
        binding.isEnableEmoji = true
        binding.isSameDate = isSameDate
        binding.enableTranslator = PreferenceHelper.defaultPreference(binding.root.context).enableTranslator &&
                transMsg == null
        binding.transMsg = transMsg
        binding.isShowTime = isShowTime

        binding.originMsg =
            if (myInfo != null && myInfo.blockUserMidList.contains(item.memberId))
                binding.root.context.getString(R.string.blocked_user_message)
            else
                super.messageParser(message!!)

        binding.originMsgLine =  binding.originMsg.lines().size
        binding.emoji.setMyMid(myInfo?.id)
    }

    private fun moveTextFragment(description: String) {
        binding.root.findNavController().navigate(R.id.action_chatFragment_to_textFragment,
            bundleOf(Constants.argKeyTitle to binding.root.context.getString(R.string.read_all),
                Constants.argKeyDescription to description,
                Constants.argKeyIsMyMessage to false,
                Constants.argKeyAutoTranslator to true))
    }

}