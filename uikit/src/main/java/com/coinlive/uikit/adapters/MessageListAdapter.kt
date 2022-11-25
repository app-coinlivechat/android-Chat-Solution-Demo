package com.coinlive.uikit.adapters

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.bindingadapterex.BindingAdapters
import com.coinlive.uikit.databinding.*
import com.coinlive.uikit.utils.PreferenceHelper
import com.coinlive.uikit.utils.PreferenceHelper.isTranslatorEnable
import com.coinlive.uikit.utils.PreferenceHelper.translatorLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


interface MessageEventListener {
    fun onClick(item: Chat, view: View)
    fun onLongClick(item: Chat, view: View)
    fun onProfileClick(item:Chat, view: View)
}

class MessageListAdapter(
    private val myInfo: CustomerUser?,
    private val coinName: String,
    private val eventListener: MessageEventListener? = null,
) :
    RecyclerView.Adapter<MessageListAdapter.BaseViewHolder>() {
    val items = ArrayList<Chat>()
    val translatorItem : HashMap<String,String> = HashMap()

    open inner class BaseViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        open fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            binding.root.setOnClickListener {
                eventListener?.onClick(item, binding.root)
                Log.e("ETGKQO", "item click!!!")
            }
            binding.root.setOnLongClickListener {
                Log.e("ETGKQO", "item long click!!!")
                eventListener?.onLongClick(item, binding.root)
                true
            }
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
        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)
            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
        }
    }

    inner class MyAssetMessageViewHolder(private val binding: ViewMyAssetChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)
            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
        }
    }

    inner class MyImageMessageViewHolder(private val binding: ViewMyImageChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)

            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clRoot)


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
                    this.adapter = ImageMessageListAdapter(item.images!!)
                }


                constraintSet.connect(binding.tvTime.id, ConstraintSet.END, binding.rvList.id, ConstraintSet.START)
                constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.rvList.id, ConstraintSet.BOTTOM)
            } else {
                constraintSet.connect(binding.tvTime.id, ConstraintSet.END, binding.ivOne.id, ConstraintSet.START)
                constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.ivOne.id, ConstraintSet.BOTTOM)
            }
            constraintSet.applyTo(binding.clRoot)
        }
    }

    inner class OtherTextMessageViewHolder(private val binding: ViewOtherTextMessageBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)
            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
            binding.enableTranslator = PreferenceHelper.defaultPreference(binding.root.context).isTranslatorEnable
            binding.ibtnProfile.setOnClickListener {
                eventListener?.onProfileClick(item,it)
            }
            if(!translatorItem.contains(item.messageId)) {
                goneTransLayout()
            } else {
                visibleTransLayout(item.messageId)
            }

            binding.ibtnTranslator.setOnClickListener {
                val originMsg = (if (binding.locale.equals("ko")) binding.chat?.koMessage else binding.chat?.enMessage)
                    ?: return@setOnClickListener
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(Coinlive.locale.language)
                    .setTargetLanguage(PreferenceHelper.defaultPreference(it.context).translatorLanguage!!)
                    .build()
                val translator = Translation.getClient(options)
                translator.translate(originMsg).addOnSuccessListener { transMsg ->
                    translatorItem[item.messageId] = transMsg
                    visibleTransLayout(item.messageId)
                    translator.close()
                }
            }
        }


        private fun visibleTransLayout(messageId : String) {
            binding.clTrans.visibility = View.VISIBLE
            binding.tvMsg.visibility = View.GONE
            binding.tvTransMsg.text = translatorItem[messageId]

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clRoot)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.clTrans.id, ConstraintSet.END)
            constraintSet.applyTo(binding.clRoot)
        }

        private fun goneTransLayout() {
            binding.clTrans.visibility = View.GONE
            binding.tvMsg.visibility = View.VISIBLE

            val constraintSet = ConstraintSet()
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.tvMsg.id, ConstraintSet.END)

            constraintSet.clone(binding.clRoot)
            constraintSet.applyTo(binding.clRoot)
        }
    }

    inner class OtherAssetMessageViewHolder(private val binding: ViewOtherAssetChatItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)
            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
            binding.base.setIsEnableTranslator(false)
        }
    }

    inner class OtherImageMessageViewHolder(private val binding: ViewOtherImageChatItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int, isSameDate: Boolean, isRoundMessage: Boolean) {
            super.bind(item, viewType, isSameDate, isRoundMessage)

            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.isRoundMessage = isRoundMessage
            binding.isSameDate = isSameDate
            binding.ibtnProfile.setOnClickListener {
                eventListener?.onProfileClick(item,it)
            }
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clRoot)


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
                    this.adapter = ImageMessageListAdapter(item.images!!)
                }


                constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.rvList.id, ConstraintSet.END)
                constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.rvList.id, ConstraintSet.BOTTOM)
            } else {
                constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.ivOne.id, ConstraintSet.END)
                constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.ivOne.id, ConstraintSet.BOTTOM)
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
            7 -> {
                // 본인 TextMessage
                val binding = ViewMyTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyTextMessageViewHolder(binding)
            }
            8 -> {
                // 본인 Asset Message
                val binding = ViewMyAssetChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyAssetMessageViewHolder(binding)
            }
            else -> {
                // 본인 이미지 Message
                val binding = ViewMyImageChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyImageMessageViewHolder(binding)
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
                if (myInfo.id == chat.memberId) getMyMessageType(chat) else getOtherMessageType(chat)
            }
        }
    }


    private fun getOtherMessageType(item: Chat): Int {
        if (item.asset != null) return 5
        if (item.images != null && item.images!!.size > 0) return 6
        return 4
    }

    private fun getMyMessageType(item: Chat): Int {
        if (item.asset != null) return 8
        if (item.images != null && item.images!!.size > 0) return 9
        return 7
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

    fun clearTansMsg() {
        translatorItem.clear()
    }

}