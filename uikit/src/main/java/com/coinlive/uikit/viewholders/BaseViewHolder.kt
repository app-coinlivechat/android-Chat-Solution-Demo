package com.coinlive.uikit.viewholders

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.uikit.adapters.MessageEventListener

interface ItemListener {
    fun getItem(position: Int) : Chat?
    fun getTranslatorItem(messageId : String): String?
    fun setTranslatorItem(messageId : String, transMsg : String)
    fun getMyInfo() : CustomerUser?
}


open class BaseViewHolder(private val binding: ViewDataBinding, private val eventListener: MessageEventListener? =
    null, private val itemListener: ItemListener?) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            itemListener?.getItem(adapterPosition)?.let {
                eventListener?.onClick(it, binding.root)
            }
        }
    }

    open fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean) {
    }

    open fun onLonClick(view: View): Boolean {
        itemListener?.getItem(adapterPosition)?.let {
            eventListener?.onLongClick(it, view,itemViewType)
        }
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