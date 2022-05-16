package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ScheduledOperationItemBinding
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.example.budgetcircle.viewmodel.models.OperationType
import com.example.budgetcircle.viewmodel.models.ScheduledOperation

class ScheduledOperationAdapter(
    var budgetTypes: Array<BudgetType>,
    var earningsTypes: Array<OperationType>,
    var expensesTypes: Array<OperationType>,
    val textPrimary: Int,
    val textSecondary: Int,
    val backgroundColor: Int,
    val borderColor: Int,
    val buttonColor: Int?
) : RecyclerView.Adapter<ScheduledOperationAdapter.ItemHolder>() {

    private var itemList = ArrayList<ScheduledOperation>()
    var onEditClick: ((item: ScheduledOperation) -> Unit)? = null
    var onDeleteClick: ((item: ScheduledOperation) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ScheduledOperationItemBinding.bind(view)
        fun bind(item: ScheduledOperation) = binding.apply {
            val title = "${item.title} (${item.sum})"
            scheduledOperationItemTitle.text = title
            scheduledOperationItemType.text =
                when (item.isExpense) {
                    true -> expensesTypes.first { it.id == item.typeId }.title
                    false -> earningsTypes.first { it.id == item.typeId }.title
                }
            scheduledOperationItemAccountType.text =
                budgetTypes.first { it.id == item.budgetTypeId }.title
            scheduledOperationItemEditButton.setOnClickListener {
                onEditClick?.invoke(item)
            }
            scheduledOperationItemDeleteButton.setOnClickListener {
                onDeleteClick?.invoke(item)
            }

            scheduledOperationItemLayout.setBackgroundColor(backgroundColor)
            scheduledOperationItemTitle.setTextColor(textPrimary)
            scheduledOperationItemTypeTitle.setTextColor(textSecondary)
            scheduledOperationItemAccountTitle.setTextColor(textSecondary)
            scheduledOperationItemType.setTextColor(textSecondary)
            scheduledOperationItemAccountType.setTextColor(textSecondary)
            scheduledOperationItemBorder.backgroundTintList = ColorStateList.valueOf(borderColor)

            scheduledOperationItemDeleteButton.backgroundTintList =
                ColorStateList.valueOf(backgroundColor)
            scheduledOperationItemEditButton.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            if (buttonColor != null) {
                scheduledOperationItemEditButton.imageTintList = ColorStateList.valueOf(buttonColor)
                scheduledOperationItemDeleteButton.imageTintList = ColorStateList.valueOf(buttonColor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scheduled_operation_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<ScheduledOperation>) {
        itemList.clear()
        for (item in list) {
            itemList.add(item)
        }
        notifyDataSetChanged()
    }
}