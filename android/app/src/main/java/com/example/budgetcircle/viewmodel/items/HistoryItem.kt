package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.HistoryItemBinding
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.example.budgetcircle.viewmodel.models.OperationType
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
    val isScheduled: Boolean,
    val color: Int
)

class HistoryAdapter(
    var budgetTypes: Array<BudgetType>,
    var earningsTypes: Array<OperationType>,
    var expensesTypes: Array<OperationType>,
    val textPrimary: Int,
    val textSecondary: Int,
    val backgroundColor: Int,
    val borderColor: Int,
    val buttonColor: Int?
) : RecyclerView.Adapter<HistoryAdapter.ItemHolder>() {
    private var itemList = ArrayList<HistoryItem>()
    var onItemClick: ((item: HistoryItem, index: Int) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = HistoryItemBinding.bind(view)
        fun bind(item: HistoryItem, to: String) = binding.apply {
            historyItemSum.text = item.sum.toString()
            historyItemColor.setBackgroundColor(item.color)
            historyItemTitle.text = item.title
            historyItemType.text =
                when (item.isExpense) {
                    true -> expensesTypes.first { it.id == item.typeId }.title
                    false -> earningsTypes.first { it.id == item.typeId }.title
                    else -> {
                        historyItemTypeTitle.text = to
                        budgetTypes.first { it.id == item.typeId }.title
                    }
                }
            historyItemAccountType.text = budgetTypes.first { it.id == item.budgetTypeId }.title
            historyItemDate.text = item.date
            historyItemLayout.setOnClickListener {
                onItemClick?.invoke(item, itemList.indexOfFirst { op -> op.id == item.id })
            }

            historyItemLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            historyItemTitle.setTextColor(textPrimary)
            historyItemSum.setTextColor(textPrimary)
            historyItemTypeTitle.setTextColor(textSecondary)
            historyItemAccountTitle.setTextColor(textSecondary)
            historyItemType.setTextColor(textSecondary)
            historyItemAccountType.setTextColor(textSecondary)
            historyItemDate.setTextColor(textSecondary)
            historyItemBorder.backgroundTintList = ColorStateList.valueOf(borderColor)

            historyItemScheduledImage.visibility = if (item.isScheduled) View.VISIBLE else View.GONE
            if (buttonColor != null) {
                historyItemScheduledImage.imageTintList = ColorStateList.valueOf(buttonColor)
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