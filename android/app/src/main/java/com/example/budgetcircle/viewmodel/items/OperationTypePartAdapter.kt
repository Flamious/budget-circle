package com.example.budgetcircle.viewmodel.items

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.OperationTypePartItemBinding
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.viewmodel.models.OperationTypePart

class OperationTypePartAdapter(
    val textPrimary: Int,
    val textSecondary: Int,
    val backgroundColor: Int,
    val borderColor: Int,
    val progressBarColor: Int,
    var progressBarNegativeColor: Int? = null
) : RecyclerView.Adapter<OperationTypePartAdapter.ItemHolder>() {
    private var itemList = ArrayList<OperationTypePart>()

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = OperationTypePartItemBinding.bind(view)

        fun bind(item: OperationTypePart) = binding.apply {
            operationTypePartItemSum.text = DoubleFormatter.formatString(item.sum)
            operationTypePartItemTitle.text = item.title
            operationTypePartItemProgressBar.progress =
                if (item.percentage > 100) 100 else item.percentage.toInt()


            operationTypePartItemLayout.setBackgroundColor(backgroundColor)
            operationTypePartItemTitle.setTextColor(textSecondary)
            operationTypePartItemSum.setTextColor(textPrimary)
            operationTypePartItemBorder.backgroundTintList = ColorStateList.valueOf(borderColor)
            operationTypePartItemProgressBar.progressDrawable.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    progressBarColor,
                    BlendModeCompat.SRC_ATOP
                )

            if (progressBarNegativeColor != null) {
                if (item.sum < 0) {
                    operationTypePartItemProgressBar.progressDrawable.colorFilter =
                        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            progressBarNegativeColor!!,
                            BlendModeCompat.SRC_ATOP
                        )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.operation_type_part_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position])

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<OperationTypePart>) {
        itemList.clear()
        itemList = ArrayList(list)
        notifyDataSetChanged()
    }
}