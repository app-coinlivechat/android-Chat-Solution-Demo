package com.coinlive.uikit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.databinding.*
import com.coinlive.uikit.viewholders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


interface MessageEventListener {
    fun onClick(item: Chat, view: View)
    fun onLongClick(item: Chat, view: View, viewType: Int, isRoundMessage: Boolean)
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
    RecyclerView.Adapter<BaseViewHolder>(), ItemListener {
    private val items = ArrayList<Chat>()
    private val translatorItem: HashMap<String, String> = HashMap()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {

            0, 1, 2, 3 -> {
                val binding = ViewServerChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ServerViewHolder(binding, coinName, eventListener, this)
            }
            4 -> {
                // 다른 사람 TextMessage
                val binding = ViewOtherTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OtherTextMessageViewHolder(binding, false, eventListener, this)
            }
            5 -> {
                // 다른 사람 Asset Message
                val binding = ViewOtherAssetChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OtherAssetMessageViewHolder(binding, false, eventListener, this)
            }
            6 -> {
                // 다른 사람 이미지 Message
                val binding = ViewOtherImageChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OtherImageMessageViewHolder(binding, false, eventListener, this)
            }
            else -> {
                // 본인 TextMessage
                val binding = ViewMyTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyTextMessageViewHolder(binding, false, eventListener, this)
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


    /**
     * 다른 유저 메세지 타입
     * 4 : 일반 Text Message
     * 5 : Asset Message
     * 6 : 이미지 Message
     */
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
            isPreviousMessageSameDate(item, previousChat),
            isRoundMessage(item, nextChat, previousChat),
            isShowTime(item, nextChat, previousChat))
    }

    override fun getItemCount(): Int = items.size

    override fun getItem(position: Int): Chat? = if (position < 0) null else if (position > items.size - 1) null else
        items[position]

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    override fun getTranslatorItem(messageId: String): String? = translatorItem[messageId]
    override fun setTranslatorItem(position: Int, messageId: String, transMsg: String) {
        translatorItem[messageId] = transMsg
        notifyItemChanged(position)
    }

    override fun getMyInfo(): CustomerUser? = myInfo

    private fun isPreviousMessageSameDate(chat: Chat, previousChat: Chat?): Boolean {
        if (previousChat == null) return false

        val previousCalendar = CalendarHelper.getCalendar(previousChat.insertTime)
        val chatCalendar = CalendarHelper.getCalendar(chat.insertTime)

        return chatCalendar[Calendar.DATE] - previousCalendar[Calendar.DATE] < 1
    }

    private fun isRoundMessage(chat: Chat, nextChat: Chat?, previousChat: Chat?): Boolean {
        return if (nextChat == null) {
            false
        } else if (previousChat == null) {
            false
        } else if (previousChat.memberId != chat.memberId) {
            false
        } else {
            chat.insertTime - previousChat.insertTime < (60 * 1000)
        }
    }

    private fun isShowTime(chat: Chat, nextChat: Chat?, previousChat: Chat?): Boolean {

        return if (nextChat == null) {
            true
        } else if (previousChat == null) {
            true
        } else if (nextChat.memberId != chat.memberId) {    // 이전 채팅 유저랑 현재 유저랑 다른 유저일 경우
            true
        } else {
            nextChat.insertTime - chat.insertTime > (60 * 1000)
        }

//        return if (nextChat == null && previousChat == null) {
//            true
//        } else if (nextChat != null && previousChat == null) {
//            nextChat.memberId == chat.memberId && nextChat.insertTime - chat.insertTime > 60000
//        } else if (nextChat == null && previousChat != null) {
//            previousChat.memberId == chat.memberId && chat.insertTime - previousChat.insertTime > 60000
//        } else {
//            val previousTimeDiff = chat.insertTime - previousChat!!.insertTime
//            previousTimeDiff < 60000
//        }
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
        val inputIndex = if (failMessageSize < 0) 0 else failMessageSize
        items.add(inputIndex, chat)
        notifyItemInserted(0)
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