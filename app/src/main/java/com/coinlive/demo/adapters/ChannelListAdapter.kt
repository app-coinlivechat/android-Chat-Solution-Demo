package com.coinlive.demo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coinlive.chat.api.model.Channel
import com.coinlive.demo.R
import com.coinlive.demo.databinding.ItemChannelBinding
import com.coinlive.demo.fragments.ChannelListFragment

interface ChannelItemOnClick {
    fun onClick(item: Channel)
}

class ChannelListAdapter(private val context: Context) :
    RecyclerView.Adapter<ChannelListAdapter.ViewHolder>() {
    val items = ArrayList<Channel>()
    var clickListener: ChannelItemOnClick? = null

    inner class ViewHolder(private val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Channel) {
            binding.tvName.text = item.name
            binding.tvSymbol.text = item.coinSymbol
            Glide.with(itemView).load(item.coinUrl).into(binding.ivImage)
            clickListener?.let { listener ->
                binding.root.setOnClickListener {
                    listener.onClick(item)
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