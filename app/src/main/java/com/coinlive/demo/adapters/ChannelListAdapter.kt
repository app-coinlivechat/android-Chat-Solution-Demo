package com.coinlive.demo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coinlive.chat.api.model.Channel
import com.coinlive.demo.R
import com.coinlive.demo.databinding.ItemChannelBinding

class ChannelListAdapter(private val context: Context) :
    RecyclerView.Adapter<ChannelListAdapter.ViewHolder>() {
    val items = ArrayList<Channel>()

    inner class ViewHolder(private val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Channel) {
            binding.tvName.text = item.name
            binding.tvSymbol.text = item.coinSymbol
            Glide.with(itemView).load(item.coinUrl).into(binding.ivImage)
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

}