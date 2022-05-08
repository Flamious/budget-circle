package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.BudgetTypeItemBinding
import com.example.budgetcircle.viewmodel.models.BudgetType
import kotlin.collections.ArrayList
import com.example.budgetcircle.settings.DoubleFormatter


class BudgetTypeAdapter(
    val textPrimary: Int,
    val textSecondary: Int,
    val backgroundColor: Int,
    val borderColor: Int,
    val buttonColor: Int?
) : RecyclerView.Adapter<BudgetTypeAdapter.ItemHolder>() {
    private var itemList = ArrayList<BudgetType>()
    var onEditClick: ((item: BudgetType) -> Unit)? = null
    var onDeleteClick: ((item: BudgetType) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = BudgetTypeItemBinding.bind(view)
        fun bind(item: BudgetType) = binding.apply {
            budgetTypeItemSum.text = DoubleFormatter.formatString(item.sum)
            budgetTypeItemTypeTitle.text = item.title
            budgetTypeItemDeleteButton.visibility = if (item.isDeletable) View.VISIBLE else View.GONE
            budgetTypeItemEditButton.visibility = if (item.isDeletable) View.VISIBLE else View.GONE
            budgetTypeItemEditButton.setOnClickListener {
                onEditClick?.invoke(item)
            }
            budgetTypeItemDeleteButton.setOnClickListener {
                onDeleteClick?.invoke(item)
            }

            budgetTypeItemDeleteButton.backgroundTintList =
                ColorStateList.valueOf(backgroundColor)
            budgetTypeItemEditButton.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            budgetTypeItemLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            if (buttonColor != null) {
                budgetTypeItemEditButton.imageTintList = ColorStateList.valueOf(buttonColor)
                budgetTypeItemDeleteButton.imageTintList = ColorStateList.valueOf(buttonColor)
            }
            budgetTypeItemSum.setTextColor(textSecondary)
            budgetTypeItemTypeTitle.setTextColor(textPrimary)
            budgetTypeItemBorder.backgroundTintList = ColorStateList.valueOf(borderColor)
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