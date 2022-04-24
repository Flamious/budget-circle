package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.OperationTypeItemBinding
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.viewmodel.models.OperationType

class OperationTypeAdapter(
    val textPrimary: Int,
    val textSecondary: Int,
    val backgroundColor: Int,
    val borderColor: Int,
    val buttonColor: Int?
) : RecyclerView.Adapter<OperationTypeAdapter.ItemHolder>() {
    private var itemList = ArrayList<OperationType>()
    var onEditClick: ((item: OperationType) -> Unit)? = null
    var onDeleteClick: ((item: OperationType) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = OperationTypeItemBinding.bind(view)

        fun bind(item: OperationType) = binding.apply {
            operationTypeItemSum.text = DoubleFormatter.formatString(item.sum)
            operationTypeItemTitle.text = item.title
            operationTypeItemDeleteButton.visibility = if (item.isDeletable) View.VISIBLE else View.GONE
            operationTypeItemEditButton.visibility = if (item.isDeletable) View.VISIBLE else View.GONE
            operationTypeItemEditButton.setOnClickListener {
                onEditClick?.invoke(item)
            }
            operationTypeItemDeleteButton.setOnClickListener {
                onDeleteClick?.invoke(item)
            }

            operationTypeItemDeleteButton.backgroundTintList =
                ColorStateList.valueOf(backgroundColor)
            operationTypeItemEditButton.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            operationTypeItemLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            if (buttonColor != null) {
                operationTypeItemEditButton.imageTintList = ColorStateList.valueOf(buttonColor)
                operationTypeItemDeleteButton.imageTintList = ColorStateList.valueOf(buttonColor)
            }
            operationTypeItemSum.setTextColor(textSecondary)
            operationTypeItemTitle.setTextColor(textPrimary)
            operationTypeItemBorderLine.backgroundTintList = ColorStateList.valueOf(borderColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.operation_type_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position])

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<OperationType>) {
        itemList.clear()
        itemList = ArrayList(list)
        notifyDataSetChanged()
    }
}