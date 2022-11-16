package com.coinlive.uikit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.databinding.*
import com.coinlive.uikit.utils.ViewUtils.margin
import kotlin.collections.ArrayList

class MessageListAdapter(private val myInfo: CustomerUser?, private val coinName:String) : RecyclerView
.Adapter<MessageListAdapter.BaseViewHolder>() {
    val items = ArrayList<Chat>()

    open inner class BaseViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        open fun bind(item: Chat, position: Int) {
        }
    }

    inner class MyMessageViewHolder(private val binding: ViewMeAssetChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, position: Int) {
            binding.chat = item
            binding.locale = "ko"
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clRoot)
            item.asset?.let {
                constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.clAsset.id, ConstraintSet.END)

            } ?: run {
                constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.tvMsg.id, ConstraintSet.END)

            }
            constraintSet.applyTo(binding.clRoot)
        }

    }

    inner class OtherViewHolder(private val binding: ViewOtherAssetChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, position: Int) {
            binding.chat = item
            binding.locale = "ko"
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clRoot)
            item.asset?.let {
                constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.clAsset.id, ConstraintSet.END)
            } ?: run {
                constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.tvMsg.id, ConstraintSet.END)
            }
            constraintSet.applyTo(binding.clRoot)
        }
    }

    inner class TwitterViewHolder(private val binding: ViewTwitterChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, position: Int) {
            binding.chat = item
            binding.locale = "ko"
            binding.coinName = coinName
        }
    }

    inner class MediumViewHolder(private val binding: ViewMediumChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, position: Int) {
            binding.chat = item
            binding.locale = "ko"
            binding.coinName = coinName
        }
    }
    inner class PriceViewHolder(private val binding: ViewPriceChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, position: Int) {
            binding.chat = item
            binding.locale = "ko"
        }
    }
    inner class FuturesViewHolder(private val binding: ViewFuturesChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, position: Int) {
            binding.chat = item
            binding.locale = "ko"
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            0 -> {
                // 본인 메세지
                val binding = ViewMeAssetChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyMessageViewHolder(binding)
            }
            1 -> {
                // 다른 사용자 메세지
                val binding = ViewOtherAssetChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return OtherViewHolder(binding)
            }
            2 -> {
                // 청산 메세지
                val binding = ViewFuturesChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return FuturesViewHolder(binding)
            }
            3 -> {
                // 트위터 메세지
                val binding = ViewTwitterChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TwitterViewHolder(binding)
            }
            4 -> {
                // 급등락
                val binding = ViewPriceChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PriceViewHolder(binding)
            }
            else -> {
                // 미디움 메세지
                val binding = ViewMediumChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MediumViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = items[position]
        return when (chat.messageType) {
            MessageType.BUY.name, MessageType.SELL.name -> 2
            MessageType.TWITTER.name -> 3
            MessageType.JUMP.name, MessageType.DROP.name -> 4
            MessageType.MEDIUM.name -> 5
            else -> {
                if(myInfo == null) return 1
                if (myInfo.id == chat.memberId) 0 else 1
            }
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

}