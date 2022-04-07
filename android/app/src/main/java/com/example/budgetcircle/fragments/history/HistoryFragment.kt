package com.example.budgetcircle.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_short_anim
        )
    }

    private val createList: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            android.R.anim.slide_in_left
        )
    }

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

    override fun onStart() {
        super.onStart()
        appear()
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
                if (it.count() == 0) {
                    if(budgetDataApi.page.value!! > 1) {
                        budgetDataApi.page.postValue(budgetDataApi.page.value!! - 1)
                    }
                    else {
                        stopLoading(true)
                    }
                } else {
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
                    stopLoading()
                }
            }
        })

        budgetDataApi.isLastPage.observe(this.viewLifecycleOwner, {
            binding.nextPageButton.isEnabled = !it
        })

        budgetDataApi.page.observe(this.viewLifecycleOwner, {
            startLoading()
            binding.previousPageButton.isEnabled = it != 1
            budgetDataApi.getOperations()
        })
    }

    //endregion
    //region Methods
    private fun createList() {
        binding.historyList.startAnimation(createList)
    }

    private fun appear() {
        binding.nextPageButton.startAnimation(appear)
        binding.filterListButton.startAnimation(appear)
        binding.previousPageButton.startAnimation(appear)
        binding.hostoryFragmentTitle.startAnimation(appear)

        createList()
    }

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

    private fun startLoading() {
        binding.progressBar3.visibility = View.VISIBLE
        binding.historyList.visibility = View.INVISIBLE
        binding.noEntriesTextView.visibility = View.GONE

        binding.previousPageButton.isEnabled = false
        binding.filterListButton.isEnabled = false
        binding.nextPageButton.isEnabled = false
    }

    private fun stopLoading(isEmpty: Boolean = false) {
        if(!isEmpty)
        {
            binding.historyList.visibility = View.VISIBLE
            createList()
        } else {
            binding.noEntriesTextView.visibility = View.VISIBLE
        }
        binding.progressBar3.visibility = View.GONE
        binding.linearLayout2.visibility = View.VISIBLE

        binding.previousPageButton.isEnabled = budgetDataApi.page.value!! > 1
        binding.filterListButton.isEnabled = true
        binding.nextPageButton.isEnabled = !budgetDataApi.isLastPage.value!!
    }
    //endregion
}