package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.OperationTypeItemBinding
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.viewmodel.models.OperationType

class OperationTypeAdapter : RecyclerView.Adapter<OperationTypeAdapter.ItemHolder>() {
    private var itemList = ArrayList<OperationType>()
    var onEditClick: ((item: OperationType) -> Unit)? = null
    var onDeleteClick: ((item: OperationType) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = OperationTypeItemBinding.bind(view)

        fun bind(item: OperationType) = binding.apply {
            operationypeSum.text = DoubleFormatter.formatString(item.sum)
            opTypeTitle.text = item.title
            operationTypeDeleteButton.isEnabled = item.isDeletable
            operationTypeEditButton.isEnabled = item.isDeletable
            operationTypeEditButton.setOnClickListener {
                onEditClick?.invoke(item)
            }
            operationTypeDeleteButton.setOnClickListener {
                onDeleteClick?.invoke(item)
            }
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