package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.HistoryItemBinding
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.example.budgetcircle.viewmodel.models.OperationType
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class HistoryItem(
    val id: Int,
    val title: String,
    val sum: Double,
    val date: String,
    val typeId: Int,
    val budgetTypeId: Int,
    val commentary: String,
    val isExpense: Boolean?,
    val color: Int,
)

class HistoryAdapter(
    var budgetTypes: Array<BudgetType>,
    var earningsTypes: Array<OperationType>,
    var expensesTypes: Array<OperationType>
) : RecyclerView.Adapter<HistoryAdapter.ItemHolder>() {
    private var itemList = ArrayList<HistoryItem>()
    var onItemClick: ((item: HistoryItem, index: Int) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = HistoryItemBinding.bind(view)
        fun bind(item: HistoryItem, to: String) = binding.apply {
            itemTitle.text = item.sum.toString()
            opColor.setBackgroundColor(item.color)
            operationTitle.text = item.title
            itemType.text =
                when (item.isExpense) {
                    true -> expensesTypes.first { it.id == item.typeId }.title
                    false -> earningsTypes.first { it.id == item.typeId }.title
                    else -> {
                        typeTitle.text = to
                        budgetTypes.first { it.id == item.typeId }.title
                    }
                }
            accountType.text = budgetTypes.first { it.id == item.budgetTypeId }.title
                /*if (item.isExpense == true) expensesTypes.first { it.id == item.typeId }.title
                else earningsTypes.first { it.id == item.typeId }.title*/
            itemDate.text = item.date
            itemLayout.setOnClickListener {
                onItemClick?.invoke(item, itemList.indexOfFirst { op -> op.id == item.id })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position], holder.itemView.context.resources.getString(R.string.to))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: Array<HistoryItem>) {
        itemList.clear()
        for (item in list) {
            itemList.add(item)
        }
        notifyDataSetChanged()
    }
}