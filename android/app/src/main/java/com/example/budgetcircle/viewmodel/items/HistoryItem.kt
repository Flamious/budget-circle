package com.example.budgetcircle.viewmodel.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.HistoryItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class HistoryItem(val id: Int, val sum: Float, val title: String, val type: String, val date: Date, val color: Int, val isRepetitive: Boolean)

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ItemHolder>() {
    private var itemList = ArrayList<HistoryItem>()

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = HistoryItemBinding.bind(view)
        fun bind(item: HistoryItem) = binding.apply {
            itemTitle.text = item.sum.toString() + "₽"
            opColor.setBackgroundColor(item.color)
            operationTitle.text = item.title
            imageRepititive.visibility = if (item.isRepetitive) View.VISIBLE else View.INVISIBLE
            itemType.text = item.type
            itemDate.text = SimpleDateFormat("dd.MM.yyy", Locale.getDefault()).format(item.date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setList(list: MutableList<HistoryItem>)
    {
        itemList.clear()
        for (item in list.reversed()) {
            itemList.add(item)
        }
        notifyDataSetChanged()
    }

    fun addItem(item: HistoryItem) {
        itemList.add(item)
        notifyDataSetChanged()
    }
}