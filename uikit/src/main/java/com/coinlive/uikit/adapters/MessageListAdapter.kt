package com.coinlive.uikit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.uikit.R
import com.coinlive.uikit.bindingadapterex.BindingAdapters
import com.coinlive.uikit.databinding.*
import kotlin.collections.ArrayList

class MessageListAdapter(private val myInfo: CustomerUser?, private val coinName:String) : RecyclerView
.Adapter<MessageListAdapter.BaseViewHolder>() {
    val items = ArrayList<Chat>()

    open inner class BaseViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        open fun bind(item: Chat, viewType: Int) {
        }
    }

    inner class ServerViewHolder(private val binding: ViewServerChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int) {
            binding.chat = item
            binding.locale = Coinlive.locale.language
            binding.coinName = coinName

            when (viewType) {
                2 -> {
                    binding.ivCon.setImageResource(R.drawable.icon_binance)
                    BindingAdapters.futureTitle(binding.tvTitle,item.messageType)
                }
                3 -> {
                    binding.ivCon.setImageResource(R.drawable.icon_twitter)
                    binding.tvTitle.text = binding.tvTitle.context.getString(R.string.twitter_chat_title,coinName,item.symbol)

                }
                4 -> {
                    binding.ivCon.setImageResource(R.drawable.icon_waring)
                    BindingAdapters.priceTitle(binding.tvTitle,item.messageType)

                }
                else -> {
                    binding.ivCon.setImageResource(R.drawable.icon_medium)
                    binding.tvTitle.text = binding.tvTitle.context.getString(R.string.medium_chat_title,coinName,item.symbol)
                }
            }
        }

    }

    inner class MyMessageViewHolder(private val binding: ViewMeAssetChatItemBinding) : BaseViewHolder(binding) {
        override fun bind(item: Chat, viewType: Int) {
            binding.chat = item
            binding.locale = Coinlive.locale.language
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
        override fun bind(item: Chat, viewType: Int) {
            binding.chat = item
            binding.locale = Coinlive.locale.language
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
            else -> {
                // 미디움 메세지
                val binding = ViewServerChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ServerViewHolder(binding)
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
        holder.bind(items[position], holder.itemViewType)
    }

    override fun getItemCount(): Int = items.size

}