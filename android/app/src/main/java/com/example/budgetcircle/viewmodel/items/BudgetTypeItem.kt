package com.example.budgetcircle.viewmodel.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.BudgetTypeItemBinding
import java.util.*
import kotlin.collections.ArrayList

data class BudgetType(val id: Int, val sum: Float, val title: String, val isDeletable: Boolean)

class BudgetTypeAdapter : RecyclerView.Adapter<BudgetTypeAdapter.ItemHolder>() {
    private var itemList = ArrayList<BudgetType>()

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = BudgetTypeItemBinding.bind(view)
        fun bind(item: BudgetType) = binding.apply {
            budgetTypeSum.text = item.sum.toString() + "₽"
            typeTitle.text = item.title
            budgetTypeDeleteButton.isEnabled = item.isDeletable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.budget_type_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setList(list: MutableList<BudgetType>)
    {
        itemList.clear()
        itemList = ArrayList(list)
        notifyDataSetChanged()
    }

    fun addItem(item: BudgetType) {
        itemList.add(item)
        notifyDataSetChanged()
    }
}