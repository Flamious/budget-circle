package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.BudgetTypeItemBinding
import com.example.budgetcircle.viewmodel.models.BudgetType
import kotlin.collections.ArrayList


class BudgetTypeAdapter : RecyclerView.Adapter<BudgetTypeAdapter.ItemHolder>() {
    private var itemList = ArrayList<BudgetType>()
    var onEditClick: ((item: BudgetType) -> Unit)? = null
    var onDeleteClick: ((item: BudgetType) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = BudgetTypeItemBinding.bind(view)
        fun bind(item: BudgetType) = binding.apply {
            budgetTypeSum.text = item.sum.toString()
            typeTitle.text = item.title
            budgetTypeDeleteButton.isEnabled = item.isDeletable
            budgetTypeEditButton.setOnClickListener {
                onEditClick?.invoke(item)
            }
            budgetTypeDeleteButton.setOnClickListener {
                onDeleteClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.budget_type_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<BudgetType>) {
        itemList.clear()
        itemList = ArrayList(list)
        notifyDataSetChanged()
    }
}