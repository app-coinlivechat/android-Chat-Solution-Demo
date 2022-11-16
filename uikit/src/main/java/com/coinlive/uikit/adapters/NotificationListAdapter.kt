package com.coinlive.uikit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.uikit.databinding.ItemNotificationBinding
import com.coinlive.uikit.models.Notification

class NotificationListAdapter : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    var items = ArrayList<Notification>()

    inner class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : Notification) {
            binding.tvTitle.text = item.name
            binding.sValue.isChecked = item.enable
            binding.sValue.setOnCheckedChangeListener { _, isChecked ->
                items[adapterPosition].enable = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun allChangeEnable(enable : Boolean) {
        items.forEach {
            it.enable = enable
        }
        notifyDataSetChanged()
    }

}