package com.coinlive.demo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coinlive.chat.api.model.Channel
import com.coinlive.demo.databinding.ItemChannelBinding

interface ChannelItemOnClick {
    fun onClick(item: Channel)
}

class ChannelListAdapter :
    RecyclerView.Adapter<ChannelListAdapter.ViewHolder>() {
    val items = ArrayList<Channel>()
    var clickListener: ChannelItemOnClick? = null
    var selectPosition = -1

    inner class ViewHolder(private val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Channel) {
            binding.tvName.text = item.name
            binding.tvSymbol.text = item.coinSymbol
            Glide.with(itemView).load(item.coinUrl).into(binding.ivImage)
            binding.rbtn.isChecked = selectPosition == adapterPosition
            clickListener?.let { listener ->
                binding.rbtn.setOnClickListener {
                    val copyLastPosition = selectPosition
                    selectPosition = adapterPosition
                    listener.onClick(item)
                    notifyItemChanged(copyLastPosition)
                    notifyItemChanged(selectPosition)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun itemOnClick(listener: ChannelItemOnClick) {
        clickListener = listener
    }

}