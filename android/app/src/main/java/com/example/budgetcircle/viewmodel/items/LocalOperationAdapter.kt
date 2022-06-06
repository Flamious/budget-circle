package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.BudgetType
import com.example.budgetcircle.database.entities.EarningType
import com.example.budgetcircle.database.entities.ExpenseType
import com.example.budgetcircle.database.entities.Operation
import com.example.budgetcircle.databinding.LocalOperationItemBinding

class LocalOperationAdapter(
    var budgetTypes: Array<BudgetType>,
    var earningsTypes: Array<EarningType>,
    var expensesTypes: Array<ExpenseType>,
    val textPrimary: Int,
    val textSecondary: Int,
    val backgroundColor: Int,
    val borderColor: Int,
    val buttonColor: Int?,
    val earningsColor: Int,
    val expenseColor: Int,
    val exchangeColor: Int,
) : RecyclerView.Adapter<LocalOperationAdapter.ItemHolder>() {

    private var itemList = ArrayList<Operation>()
    var onEditClick: ((item: Operation) -> Unit)? = null
    var onDeleteClick: ((item: Operation) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = LocalOperationItemBinding.bind(view)
        fun bind(item: Operation, to: String) = binding.apply {
            val title = "${item.title} (${item.sum})"

            localOperationItemTitle.text = title
            localOperationItemType.text =
                when (item.isExpense) {
                    true -> {
                        localOperationItemColor.setBackgroundColor(expenseColor)
                        expensesTypes.first { it.id == item.typeId }.title
                    }
                    false -> {
                        localOperationItemColor.setBackgroundColor(earningsColor)
                        earningsTypes.first { it.id == item.typeId }.title
                    }
                    else -> {
                        localOperationItemColor.setBackgroundColor(exchangeColor)
                        localOperationItemTypeTitle.text = to
                        budgetTypes.first { it.id == item.typeId }.title
                    }
                }
            localOperationItemAccountType.text = budgetTypes.first { it.id == item.budgetTypeId }.title

            localOperationItemEditButton.setOnClickListener {
                onEditClick?.invoke(item)
            }
            localOperationItemDeleteButton.setOnClickListener {
                onDeleteClick?.invoke(item)
            }

            localOperationItemLayout.setBackgroundColor(backgroundColor)
            //localOperationItemLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            localOperationItemTitle.setTextColor(textPrimary)
            localOperationItemTypeTitle.setTextColor(textSecondary)
            localOperationItemAccountTitle.setTextColor(textSecondary)
            localOperationItemType.setTextColor(textSecondary)
            localOperationItemAccountType.setTextColor(textSecondary)
            localOperationItemBorder.backgroundTintList = ColorStateList.valueOf(borderColor)

            localOperationItemDeleteButton.backgroundTintList =
                ColorStateList.valueOf(backgroundColor)
            localOperationItemEditButton.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            if (buttonColor != null) {
                localOperationItemEditButton.imageTintList = ColorStateList.valueOf(buttonColor)
                localOperationItemDeleteButton.imageTintList = ColorStateList.valueOf(buttonColor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.local_operation_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position], holder.itemView.context.resources.getString(R.string.to))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Operation>) {
        itemList.clear()
        for (item in list) {
            itemList.add(item)
        }
        notifyDataSetChanged()
    }
}