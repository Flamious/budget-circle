package com.example.budgetcircle.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentHistoryBinding
import com.example.budgetcircle.viewmodel.BudgetData
/*import com.example.budgetcircle.viewmodel.items.BudgetType*/
import com.example.budgetcircle.viewmodel.items.HistoryAdapter
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.util.*

class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding
    private val adapter = HistoryAdapter()
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        init()

        budgetData.historyItems.observe(this.viewLifecycleOwner, {
            val list: Array<HistoryItem> = Array(it.size) { index ->

                HistoryItem(
                    it[index].id,
                    it[index].title,
                    it[index].sum,
                    it[index].date,
                    if (it[index].isExpense) budgetData.expenseTypes.first { type -> type.id == it[index].typeId }.title else budgetData.earningTypes.first { type -> type.id == it[index].typeId }.title,
                    budgetData.budgetTypes.value!!.first { type -> type.id == it[index].budgetTypeId }.title,
                    it[index].commentary,
                    it[index].isRepetitive,
                    it[index].isExpense,
                    ContextCompat.getColor(
                        this.requireContext(),
                        if (it[index].isExpense) R.color.red_switch_main else R.color.blue_switch_main
                    )
                )

            }
            adapter.setList(list)
        })
        return binding.root
    }

    private fun init() {
        binding.apply {
            historyList.layoutManager = GridLayoutManager(this@HistoryFragment.context, 1)
            historyList.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}