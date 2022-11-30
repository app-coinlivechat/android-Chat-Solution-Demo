package com.coinlive.uikit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.api.model.ReportType
import com.coinlive.uikit.databinding.ItemReportBinding

class ReportListAdapter() : RecyclerView.Adapter<ReportListAdapter.ViewHolder>() {
    private var items = ArrayList<ReportType>()
    private var selectPosition = -1
    var selectType: ReportType? = null
        private set(value) {
            field = value
        }

    inner class ViewHolder(private val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReportType) {
            binding.text = item.type
            binding.rbtn.isChecked = selectType?.typeId == item.typeId
            binding.root.setOnClickListener {
                clickItem(item)
            }
            binding.rbtn.setOnClickListener {
                clickItem(item)
            }
        }

        private fun clickItem(item: ReportType) {
            val copyLastPosition = selectPosition
            selectPosition = adapterPosition
            selectType = item
            notifyItemChanged(copyLastPosition)
            notifyItemChanged(selectPosition)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(items: ArrayList<ReportType>) {
        this.items = items
    }
}