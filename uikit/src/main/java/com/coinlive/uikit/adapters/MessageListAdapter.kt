package com.coinlive.uikit.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.bindingadapterex.BindingAdapters
import com.coinlive.uikit.databinding.*
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.PreferenceHelper
import com.coinlive.uikit.utils.PreferenceHelper.enableTranslator
import com.coinlive.uikit.utils.PreferenceHelper.translatorLanguage
import com.coinlive.uikit.views.OnEmojiEventListener
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


interface MessageEventListener {
    fun onClick(item: Chat, view: View)
    fun onLongClick(item: Chat, view: View)
    fun onProfileClick(item: Chat, view: View)
    fun addEmoji(item: Chat, emojiKey: String)
    fun deleteEmoji(item: Chat, emojiKey: String)
    fun onClickDelete(item: Chat, view: View)
    fun onClickRetry(item: Chat, view: View)
}

class MessageListAdapter(
    private var myInfo: CustomerUser?,
    private val coinName: String,
    private val eventListener: MessageEventListener? = null,
) :
    RecyclerView.Adapter<MessageListAdapter.BaseViewHolder>() {
    private val items = ArrayList<Chat>()
    val translatorItem: HashMap<String, String> = HashMap()
    private val attachedPosition = ArrayList<Int>()
    private var failMessageSize = 0


    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        attachedPosition.add(holder.adapterPosition)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        super.onViewDetachedFromWindow(holder)
        attachedPosition.remove(holder.adapterPosition)
    }

    open inner class BaseViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                eventListener?.onClick(item = items[adapterPosition], binding.root)
            }
        }

        open fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
        }

        open fun onLonClick(view: View): Boolean {
            eventListener?.onLongClick(items[adapterPosition], view)
            return true
        }

        open fun messageParser(message: String): String {
            Regex(":CL\\\$([a-zA-Z]*)-([A-Z]*)-([A-Z]*)\\|(\\d*)CL:").find(message)?.groups?.let { matchGroup ->
                return "$${matchGroup[1]!!.value}-${matchGroup[2]!!.value}-${matchGroup[3]!!.value}"
            }

            Regex(":CL@([\\da-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]*)\\_([\\d]*)CL:").find(message)?.groups?.let { matchGroup ->
                return "@${matchGroup[1]!!.value}"
            }

            return message
        }
    }

    inner class ServerViewHolder(private val binding: ViewServerChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)
            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.coinName = coinName
            binding.isSameDate = isSameDate

            when (viewType) {
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

    inner class MyTextMessageViewHolder(private val binding: ViewMyTextMessageBinding) : BaseViewHolder(binding) {

        init {
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
                eventListener?.onClickDelete(items[adapterPosition], it)
            }
            binding.ibtnRetry.setOnClickListener {
                eventListener?.onClickRetry(items[adapterPosition], it)
            }
        }

        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)
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

    inner class OtherTextMessageViewHolder(private val binding: ViewOtherTextMessageBinding) : BaseViewHolder(binding) {
        init {
            binding.clMaxMsg.setOnLongClickListener { onLonClick(it) }

            binding.clTrans.setOnLongClickListener { onLonClick(it) }

            binding.tvMsg.setOnLongClickListener { onLonClick(it) }

            binding.clMaxMsg.setOnClickListener {
                it.findNavController().navigate(R.id.action_chatFragment_to_textFragment, bundleOf(Constants
                    .argKeyTitle to it.context.getString(R.string.read_all),
                    Constants.argKeyDescription to binding.originMsg))
            }
            binding.ibtnProfile.setOnClickListener {
                eventListener?.onProfileClick(items[adapterPosition], it)
            }

            binding.emoji.setEmojiListener(object : OnEmojiEventListener {
                override fun addEmoji(key: String) {
                    eventListener?.addEmoji(items[adapterPosition], key)
                }

                override fun deleteEmoji(key: String) {
                    eventListener?.deleteEmoji(items[adapterPosition], key)
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
                        translatorItem[items[adapterPosition].messageId] = transMsg
                        visibleTransLayout(items[adapterPosition].messageId)
                        translator.close()
                    }
                }
            }
        }


        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)

            val transMsg = translatorItem[item.messageId]
            val message = if (Coinlive.locale.language.equals("ko")) item.koMessage else item.enMessage ?: ""

            binding.chat = item
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
            binding.enableTranslator = PreferenceHelper.defaultPreference(binding.root.context).enableTranslator &&
                    transMsg == null
            binding.transMsg = transMsg
            binding.originMsg =
                if (myInfo != null && myInfo!!.blockUserMidList.contains(item.memberId)) "차단 사용자의 메세지입니다." else super
                    .messageParser(message!!)

            binding.emoji.setMyMid(myInfo?.id)

            if (binding.originMsg!!.length > 400) {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.clRoot)
                constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.clMaxMsg.id, ConstraintSet.END)
                constraintSet.connect(binding.tvTime.id,
                    ConstraintSet.BOTTOM,
                    binding.clMaxMsg.id,
                    ConstraintSet.BOTTOM)
                constraintSet.connect(binding.ibtnTranslator.id,
                    ConstraintSet.START,
                    binding.clMaxMsg.id,
                    ConstraintSet.END)
                constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.clMaxMsg.id, ConstraintSet.BOTTOM)
                constraintSet.applyTo(binding.clRoot)
            } else if (transMsg == null) {
                goneTransLayout()
            } else {
                visibleTransLayout(item.messageId)
            }

        }

        private fun moveTextFragment(description: String) {
            binding.root.findNavController().navigate(R.id.action_chatFragment_to_textFragment,
                bundleOf(Constants.argKeyTitle to binding.root.context.getString(R.string.read_all),
                    Constants.argKeyDescription to description,
                    Constants.argKeyIsMyMessage to false,
                    Constants.argKeyAutoTranslator to true))
        }


        private fun visibleTransLayout(messageId: String) {
            binding.transMsg = translatorItem[messageId]
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clRoot)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.clTrans.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.clTrans.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.clTrans.id, ConstraintSet.BOTTOM)

            constraintSet.applyTo(binding.clRoot)
        }

        private fun goneTransLayout() {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clRoot)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.tvMsg.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.tvMsg.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.ibtnTranslator.id, ConstraintSet.START, binding.tvMsg.id, ConstraintSet.END)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.tvMsg.id, ConstraintSet.BOTTOM)

            constraintSet.applyTo(binding.clRoot)
        }
    }

    inner class OtherAssetMessageViewHolder(private val binding: ViewOtherAssetChatItemBinding) :
        BaseViewHolder(binding) {

        init {
            binding.root.setOnLongClickListener {
                eventListener?.onLongClick(items[adapterPosition], binding.clAsset)
                true
            }

            binding.emoji.setEmojiListener(object : OnEmojiEventListener {
                override fun addEmoji(key: String) {
                    eventListener?.addEmoji(items[adapterPosition], key)
                }

                override fun deleteEmoji(key: String) {
                    eventListener?.deleteEmoji(items[adapterPosition], key)
                }
            })
        }

        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)
            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
            val price = if (binding.locale!! == "ko") item.asset!!.priceWon else item.asset!!.priceDol

            binding.price = "$${item.symbol} ${String.format("%.4f", item.asset!!.amount)} (${
                NumberFormat.getCurrencyInstance(Coinlive.locale).format(price)
            })"
            binding.base.setIsEnableTranslator(false)
            binding.emoji.setMyMid(myInfo?.id)
        }
    }

    inner class OtherImageMessageViewHolder(private val binding: ViewOtherImageChatItemBinding) :
        BaseViewHolder(binding) {
        init {
            binding.ivOne.setOnLongClickListener { onLonClick(it) }

            binding.ibtnProfile.setOnClickListener {
                eventListener?.onProfileClick(items[adapterPosition], it)
            }

            binding.emoji.setEmojiListener(object : OnEmojiEventListener {
                override fun addEmoji(key: String) {
                    eventListener?.addEmoji(items[adapterPosition], key)
                }

                override fun deleteEmoji(key: String) {
                    eventListener?.deleteEmoji(items[adapterPosition], key)
                }
            })
        }

        private fun itemImageLonClick() {
            onLonClick(binding.rvList)
        }

        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)


            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
            binding.isBlockUser = myInfo?.blockUserMidList?.contains(item.memberId)

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
                val layoutManager = GridLayoutManager(binding.rvList.context, if (item.images!!.size > 6) 3 else 2)
                binding.rvList.apply {
                    this.layoutManager = layoutManager
                    this.setPadding(0)
                    this.addItemDecoration(object : ItemDecoration() {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {

            0, 1, 2, 3 -> {
                val binding = ViewServerChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ServerViewHolder(binding)
            }
            4 -> {
                // 다른 사람 TextMessage
                val binding = ViewOtherTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OtherTextMessageViewHolder(binding)
            }
            5 -> {
                // 다른 사람 Asset Message
                val binding = ViewOtherAssetChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OtherAssetMessageViewHolder(binding)
            }
            6 -> {
                // 다른 사람 이미지 Message
                val binding = ViewOtherImageChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OtherImageMessageViewHolder(binding)
            }
            else -> {
                // 본인 TextMessage
                val binding = ViewMyTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyTextMessageViewHolder(binding)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val chat = items[position]
        return when (chat.messageType) {
            MessageType.BUY.name, MessageType.SELL.name -> 0
            MessageType.TWITTER.name -> 1
            MessageType.JUMP.name, MessageType.DROP.name -> 2
            MessageType.MEDIUM.name -> 3
            else -> {
                if (myInfo == null) return getOtherMessageType(chat)
                if (myInfo!!.id == chat.memberId) 7 else getOtherMessageType(chat)
            }
        }
    }


    private fun getOtherMessageType(item: Chat): Int {
        if (item.asset != null) {
            if (myInfo != null && myInfo!!.blockUserMidList.contains(item.memberId)) return 4
            return 5
        }
        if (item.images != null && item.images!!.size > 0) return 6
        return 4
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = items[position]
        val previousIndex = position + 1 // list가 역순이라서 +1
        val nextIndex = position - 1    // list가 역순이라서 -1
        var nextChat: Chat? = null
        var previousChat: Chat? = null

        if (items.size - 1 >= previousIndex) {
            previousChat = items[previousIndex]
        }
        if (nextIndex > 0) {
            nextChat = items[nextIndex]
        }

        holder.bind(item,
            holder.itemViewType,
            isPreviousMessageSameDate(item, previousChat),
            isRoundMessage(item, nextChat, previousChat))
    }

    override fun getItemCount(): Int = items.size

    private fun isPreviousMessageSameDate(chat: Chat, previousChat: Chat?): Boolean {
        if (previousChat == null) return false

        val previousCalendar = CalendarHelper.getCalendar(previousChat.insertTime)
        val chatCalendar = CalendarHelper.getCalendar(chat.insertTime)

        return chatCalendar[Calendar.DATE] - previousCalendar[Calendar.DATE] < 1
    }

    private fun isRoundMessage(chat: Chat, nextChat: Chat?, previousChat: Chat?): Boolean {
        return if (nextChat == null && previousChat == null) {
            true
        } else if (nextChat != null && previousChat == null) {
            nextChat.memberId == chat.memberId && nextChat.insertTime - chat.insertTime > 60000
        } else if (nextChat == null && previousChat != null) {
            previousChat.memberId == chat.memberId && chat.insertTime - previousChat.insertTime > 60000
        } else {
            val previousTimeDiff = chat.insertTime - previousChat!!.insertTime
            previousTimeDiff < 60000
        }
    }

    private fun isShowTime(chat: Chat, nextChat: Chat?, previousChat: Chat?): Boolean {
        return if (nextChat == null && previousChat == null) {
            true
        } else if (nextChat != null && previousChat == null) {
            nextChat.memberId == chat.memberId && nextChat.insertTime - chat.insertTime > 60000
        } else if (nextChat == null && previousChat != null) {
            previousChat.memberId == chat.memberId && chat.insertTime - previousChat.insertTime > 60000
        } else {
            val previousTimeDiff = chat.insertTime - previousChat!!.insertTime
            previousTimeDiff < 60000
        }
    }


    fun addBlockUser(myInfo: CustomerUser, mId: String) {
        this.myInfo = myInfo
        attachedPosition.forEach {
            if (items[it].memberId == mId) {
                LoggerHelper.d("change item!!")
                notifyItemChanged(it)
            }
        }
    }

    fun deleteBlockUser(myInfo: CustomerUser, mId: String) {
        this.myInfo = myInfo
        attachedPosition.forEach {
            if (items[it].memberId == mId) {
                LoggerHelper.d("change item!!")
                notifyItemChanged(it)
            }
        }
    }

    fun clearTansMsg() {
        translatorItem.clear()
    }

    fun addNewItem(chat: Chat) {
        LoggerHelper.d("addNewItem inputIndex : $failMessageSize, id : ${chat.messageId}, message : ${chat.koMessage}")

        items.add(failMessageSize, chat)
        notifyItemRangeInserted(failMessageSize, 1)
    }

    fun addFailItem(chat: Chat) {

        failMessageSize += 1
        items.add(0, chat)
        notifyItemInserted(0)
    }

    fun addFailItems(list: ArrayList<Chat>) {
        failMessageSize += list.size
        items.addAll(0, list)
        notifyItemInserted(0)
    }

    fun deleteFailItem(messageId: String) {
        val index = items.indexOfFirst { it.messageId == messageId }
        if (index > -1) {
            failMessageSize -= 1

            items.removeAt(index)
            CoroutineScope(Dispatchers.Main).launch {
                notifyItemRemoved(index)
            }
        }
    }

    fun modifyMessage(chat: Chat) {
        val index = items.indexOfFirst { it.messageId == chat.messageId }
        if (index > -1) {
            items[index] = chat
            notifyItemChanged(index)
        }
    }

    fun addOldMessage(chatList: ArrayList<Chat>) {
        val pushIndex = items.size
        items.addAll(pushIndex, chatList)
        notifyItemRangeChanged(if (pushIndex == 0) 0 else pushIndex - 1, chatList.size)
    }

}