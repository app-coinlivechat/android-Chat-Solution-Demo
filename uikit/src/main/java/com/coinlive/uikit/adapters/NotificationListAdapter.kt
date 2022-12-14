package com.coinlive.uikit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.databinding.ItemNotificationBinding
import com.coinlive.uikit.models.Notification


interface AllItemChangeListener{
    fun allItemChange(enable: Boolean)
}

class NotificationListAdapter(private val listener: AllItemChangeListener) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    var items = ArrayList<Notification>()

    private val enablesId = arrayListOf<String>()

    inner class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.sValue.setOnClickListener {
                val item = items[adapterPosition]
                item.enable = !item.enable
                if(item.enable) {
                    enablesId.add(item.id)
                } else {
                    enablesId.remove(item.id)
                }
                LoggerHelper.de(enablesId.toString())
                if(enablesId.size == items.size) {
                    listener.allItemChange(true)
                } else {
                    listener.allItemChange(false)
                }
            }
        }

        fun bind(item : Notification) {
            LoggerHelper.de("${item.id} : ${item.enable}")

            binding.tvTitle.text = item.name
            binding.sValue.isChecked = item.enable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addAllItems(list : ArrayList<Notification>) {
        list.forEach {
            if(it.enable) {
                enablesId.add(it.id)
            }
        }
        items.addAll(list)
        notifyItemRangeInserted(0,list.size)
    }

    fun allChangeEnable(enable : Boolean) {
        items.forEachIndexed { index, notification ->
            if(enable) {
                enablesId.add(notification.id)
            } else {
                enablesId.remove(notification.id)
            }
            notification.enable = enable
            notifyItemChanged(index)
        }
    }



}