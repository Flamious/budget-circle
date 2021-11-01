package com.example.budgetcircle.viewmodel.items

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.databinding.BudgetTypeItemBinding
import kotlin.collections.ArrayList
/*

data class BudgetType(var id: Int, var sum: Float, var title: String?, var isDeletable: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeFloat(sum)
        parcel.writeString(title)
        parcel.writeByte(if (isDeletable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BudgetType> {
        override fun createFromParcel(parcel: Parcel): BudgetType {
            return BudgetType(parcel)
        }

        override fun newArray(size: Int): Array<BudgetType?> {
            return arrayOfNulls(size)
        }
    }
}
*/

class BudgetTypeAdapter : RecyclerView.Adapter<BudgetTypeAdapter.ItemHolder>() {
    private var itemList = ArrayList<BudgetType>()
    var onEditClick: ((item: BudgetType) -> Unit)? = null
    var onDeleteClick: ((item: BudgetType) -> Unit)? = null

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = BudgetTypeItemBinding.bind(view)
        fun bind(item: BudgetType) = binding.apply {
            budgetTypeSum.text = item.sum.toString() + "₽"
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.budget_type_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setList(list: List<BudgetType>)
    {
        itemList.clear()
        itemList = ArrayList(list)
        notifyDataSetChanged()
    }

    fun addItem(item: BudgetType) {
        itemList.add(item)
        notifyDataSetChanged()
    }
}