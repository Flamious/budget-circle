package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.entities.types.EarningType
import com.example.budgetcircle.database.entities.types.ExpenseType
import com.example.budgetcircle.databinding.HistoryItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class HistoryItem(
    val id: Int,
    val title: String,
    val sum: Double,
    val date: Date,
    val typeId: Int,
    val budgetTypeId: Int,
    val commentary: String,
    val isRepetitive: Boolean,
    val isExpense: Boolean?,
    val color: Int,
)

class HistoryAdapter(
    var budgetTypes: Array<BudgetType>,
    var earningsTypes: Array<EarningType>,
    var expensesTypes: Array<ExpenseType>
) : RecyclerView.Adapter<HistoryAdapter.ItemHolder>() {
    private var itemList = ArrayList<HistoryItem>()
    var onItemClick: ((item: HistoryItem, index: Int) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = HistoryItemBinding.bind(view)
        fun bind(item: HistoryItem, to: String) = binding.apply {
            itemTitle.text = item.sum.toString()
            opColor.setBackgroundColor(item.color)
            operationTitle.text = item.title
            imageRepetitive.visibility = if (item.isRepetitive) View.VISIBLE else View.INVISIBLE
            itemType.text =
                when (item.isExpense) {
                    true -> {
                        if(MainActivity.isRu()) expensesTypes.first { it.id == item.typeId }.titleRu
                        else expensesTypes.first { it.id == item.typeId }.title
                    }
                    false -> {
                        if(MainActivity.isRu()) earningsTypes.first { it.id == item.typeId }.titleRu
                        else earningsTypes.first { it.id == item.typeId }.title
                    }
                    else -> {
                        typeTitle.text = to
                        if(MainActivity.isRu()) budgetTypes.first { it.id == item.typeId }.titleRu
                        else budgetTypes.first { it.id == item.typeId }.title
                    }
                }
            if(MainActivity.isRu()) accountType.text = budgetTypes.first { it.id == item.budgetTypeId }.titleRu
            else accountType.text = budgetTypes.first { it.id == item.budgetTypeId }.title
            itemDate.text = SimpleDateFormat("dd.MM.yyy", Locale.getDefault()).format(item.date)
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
        for (item in list.reversed()) {
            itemList.add(item)
        }
        notifyDataSetChanged()
    }
}