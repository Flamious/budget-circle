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
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.items.HistoryAdapter
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.util.*

class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: HistoryAdapter

    private val budgetDataApi: BudgetDataApi by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater)
        setAdapter()
        setButtons()
        setObservation()
        return binding.root
    }

    //region Setting
    private fun setAdapter() {
        adapter = HistoryAdapter(
            budgetDataApi.budgetTypes.value!!.toTypedArray(),
            budgetDataApi.earningTypes.value!!.toTypedArray(),
            budgetDataApi.expenseTypes.value!!.toTypedArray()
        )
        binding.apply {
            historyList.layoutManager = GridLayoutManager(this@HistoryFragment.context, 1)
            historyList.adapter = adapter
            if (budgetDataApi.chosenHistoryItemIndex.value != null) {
                budgetDataApi.chosenHistoryItemIndex.postValue(null)
            }
        }
    }

    private fun setButtons() {
        adapter.onItemClick = { item, index ->
            budgetDataApi.chosenHistoryItem.value = item
            budgetDataApi.chosenHistoryItemIndex.value = index
            openInfo()
        }

        binding.nextPageButton.setOnClickListener {
            budgetDataApi.page.postValue(budgetDataApi.page.value!! + 1)
        }

        binding.filterListButton.setOnClickListener {
            openFilter()
        }

        binding.previousPageButton.setOnClickListener {
            budgetDataApi.page.postValue(budgetDataApi.page.value!! - 1)
        }
    }

    private fun setObservation() {
        budgetDataApi.operations.observe(this.viewLifecycleOwner, {
            if (it != null) {
                val list: Array<HistoryItem> = Array(it.size) { index ->
                    HistoryItem(
                        it[index].id,
                        it[index].title,
                        it[index].sum,
                        it[index].date,
                        it[index].typeId,
                        it[index].budgetTypeId,
                        it[index].commentary,
                        it[index].isExpense,
                        ContextCompat.getColor(
                            this.requireContext(),
                            when (it[index].isExpense) {
                                true -> R.color.red_main
                                false -> R.color.blue_main
                                else -> R.color.green_main
                            }
                        )
                    )
                }

                adapter.setList(list)
                binding.historyList.scrollToPosition(0)
            }
        })

        budgetDataApi.isLastPage.observe(this.viewLifecycleOwner, {
            binding.nextPageButton.isEnabled = !it
        })

        budgetDataApi.page.observe(this.viewLifecycleOwner, {
            binding.previousPageButton.isEnabled = it != 1
            budgetDataApi.getOperations()
        })
    }

    //endregion
    //region Methods
    private fun openInfo() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, OperationInfoFragment())
            ?.commit()
    }

    private fun openFilter() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, OperationListSettingsFragment())
            ?.commit()
    }
    //endregion
}