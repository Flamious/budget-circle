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
import com.example.budgetcircle.viewmodel.items.HistoryAdapter
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.util.*

class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater)
        adapter = HistoryAdapter(
            budgetData.budgetTypes.value!!.toTypedArray(),
            budgetData.earningTypes.toTypedArray(),
            budgetData.expenseTypes.toTypedArray()
        )
        init()
        setButtons()
        budgetData.historyItems.observe(this.viewLifecycleOwner, {
            val list: Array<HistoryItem> = Array(it.size) { index ->
                HistoryItem(
                    it[index].id,
                    it[index].title,
                    it[index].sum,
                    it[index].date,
                    it[index].typeId,
                    it[index].budgetTypeId,
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

    private fun setButtons() {
        adapter.onItemClick = { item, index ->
            budgetData.chosenHistoryItem.value = item
            budgetData.chosenHistoryItemIndex.value = index
            openInfo()
        }
    }

    private fun openInfo() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, OperationInfoFragment())
            ?.commit()
    }

    private fun init() {
        binding.apply {
            historyList.layoutManager = GridLayoutManager(this@HistoryFragment.context, 1)
            historyList.adapter = adapter
            if (budgetData.chosenHistoryItemIndex.value != null) {
                historyList.scrollToPosition(
                    budgetData.chosenHistoryItemIndex.value!!
                )
                budgetData.chosenHistoryItemIndex.postValue(null)
            }
        }
    }
}