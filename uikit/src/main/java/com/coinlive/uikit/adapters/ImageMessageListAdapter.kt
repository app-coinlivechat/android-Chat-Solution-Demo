package com.coinlive.uikit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.databinding.ItemImageBinding
import com.coinlive.uikit.utils.ViewUtils.dpToPx
import com.coinlive.uikit.utils.ViewUtils.layoutParams

class ImageMessageListAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<ImageMessageListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            LoggerHelper.d("url : $url")
            binding.url = url
            binding.root.layoutParams<ViewGroup.LayoutParams> {
                val a = itemCount % 2
                if(a == 0) {    //짝수
                    if (itemCount < 6) {
                        width = binding.root.dpToPx(130F)
                        height = binding.root.dpToPx(130F)
                    } else {
                        width = if(adapterPosition == itemCount -1) binding.root.dpToPx(265F) else binding.root
                            .dpToPx(90F)
                        height = binding.root.dpToPx(90F)
                    }
                } else {    // 홀수
                    width = if(adapterPosition == itemCount -1) binding.root.dpToPx(265F) else binding.root.dpToPx(130F)
                    height = binding.root.dpToPx(121F)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}