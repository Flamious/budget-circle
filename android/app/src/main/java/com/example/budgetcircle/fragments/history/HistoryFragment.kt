package com.example.budgetcircle.fragments.history

import android.content.res.ColorStateList
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
import com.example.budgetcircle.fragments.UserFragment
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
        setTheme()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setAdapter() {
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val borderColor: Int

        if (BudgetDataApi.mode.value!! == UserFragment.DAY) {
            textPrimary = ContextCompat.getColor(this.requireContext(), R.color.text_primary)
            textSecondary = ContextCompat.getColor(this.requireContext(), R.color.text_secondary)
            backgroundColor = ContextCompat.getColor(this.requireContext(), R.color.white)
            borderColor = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
        } else {
            textPrimary = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
            textSecondary = ContextCompat.getColor(this.requireContext(), R.color.grey)
            backgroundColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
            borderColor = ContextCompat.getColor(this.requireContext(), R.color.dark_grey)
        }

        adapter = HistoryAdapter(
            budgetDataApi.budgetTypes.value!!.toTypedArray(),
            budgetDataApi.earningTypes.value!!.toTypedArray(),
            budgetDataApi.expenseTypes.value!!.toTypedArray(),
            textPrimary,
            textSecondary,
            backgroundColor,
            borderColor
        )

        binding.apply {
            historyFragmentList.layoutManager = GridLayoutManager(this@HistoryFragment.context, 1)
            historyFragmentList.adapter = adapter
            if (budgetDataApi.chosenHistoryItemIndex.value != null) {
                budgetDataApi.chosenHistoryItemIndex.postValue(null)
            }
        }
    }

    private fun setTheme() {
        if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
            binding.apply {
                val textPrimary = ContextCompat.getColor(
                    this@HistoryFragment.requireContext(),
                    R.color.light_grey
                )
                val backgroundColor = ContextCompat.getColor(
                    this@HistoryFragment.requireContext(),
                    R.color.dark_grey
                )
                val mainColor = ContextCompat.getColor(
                    this@HistoryFragment.requireContext(),
                    R.color.darker_grey
                )

                historyFragmentHeaderLayout.setBackgroundColor(mainColor)
                historyFragmentFilterListButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                historyFragmentOpenChartButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                historyFragmentNextPageButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                historyFragmentNextPageButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                historyFragmentPreviousPageButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                historyFragmentPreviousPageButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                historyFragmentPageLayout.setBackgroundColor(mainColor)
                historyFragmentList.setBackgroundColor(backgroundColor)
                historyFragmentPageNumberTextView.setTextColor(textPrimary)
                historyFragmentLayout.setBackgroundColor(backgroundColor)
                historyFragmentProgressBar.indeterminateTintList =
                    ColorStateList.valueOf(mainColor)
            }
        }
    }

    private fun setButtons() {
        adapter.onItemClick = { item, index ->
            budgetDataApi.chosenHistoryItem.value = item
            budgetDataApi.chosenHistoryItemIndex.value = index
            openInfo()
        }

        binding.historyFragmentNextPageButton.setOnClickListener {
            budgetDataApi.page.postValue(budgetDataApi.page.value!! + 1)
        }

        binding.historyFragmentFilterListButton.setOnClickListener {
            openFilter()
        }

        binding.historyFragmentPreviousPageButton.setOnClickListener {
            budgetDataApi.page.postValue(budgetDataApi.page.value!! - 1)
        }

        binding.historyFragmentOpenChartButton.setOnClickListener {
            openChart()
        }
    }

    private fun setObservation() {
        budgetDataApi.operations.observe(this.viewLifecycleOwner, {
            if (it != null) {
                if (it.count() == 0) {
                    if (budgetDataApi.page.value!! > 1) {
                        budgetDataApi.page.postValue(budgetDataApi.page.value!! - 1)
                    } else {
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
                                if (BudgetDataApi.mode.value!! == UserFragment.DAY)
                                    when (it[index].isExpense) {
                                        true -> R.color.red_main
                                        false -> R.color.blue_main
                                        else -> R.color.green_main
                                    }
                                else
                                    when (it[index].isExpense) {
                                        true -> R.color.red_secondary
                                        false -> R.color.blue_secondary
                                        else -> R.color.green_secondary
                                    }
                            )
                        )
                    }

                    adapter.setList(list)
                    binding.historyFragmentList.scrollToPosition(0)
                    stopLoading()
                }
            }
        })

        budgetDataApi.isLastPage.observe(this.viewLifecycleOwner, {
            binding.historyFragmentNextPageButton.isEnabled = !it
        })

        budgetDataApi.page.observe(this.viewLifecycleOwner, {
            startLoading()
            binding.historyFragmentPreviousPageButton.isEnabled = it != 1
            binding.historyFragmentPageNumberTextView.text = it.toString()
            budgetDataApi.getOperations()
        })
    }

    //endregion
    //region Methods
    private fun createList() {
        binding.historyFragmentList.startAnimation(createList)
    }

    private fun appear() {
        binding.historyFragmentNextPageButton.startAnimation(appear)
        binding.historyFragmentFilterListButton.startAnimation(appear)
        binding.historyFragmentPreviousPageButton.startAnimation(appear)
        binding.historyFragmentHeaderLayout.startAnimation(appear)

        createList()
    }

    private fun openInfo() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, OperationInfoFragment())
            ?.commit()
    }

    private fun openChart() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryChartFragment())
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
        binding.historyFragmentProgressBar.visibility = View.VISIBLE
        binding.historyFragmentList.visibility = View.INVISIBLE
        binding.historyFragmentNoEntriesTextView.visibility = View.GONE

        binding.historyFragmentPreviousPageButton.isEnabled = false
        binding.historyFragmentFilterListButton.isEnabled = false
        binding.historyFragmentNextPageButton.isEnabled = false
    }

    private fun stopLoading(isEmpty: Boolean = false) {
        if (!isEmpty) {
            binding.historyFragmentList.visibility = View.VISIBLE
            createList()
        } else {
            binding.historyFragmentNoEntriesTextView.visibility = View.VISIBLE
        }
        binding.historyFragmentProgressBar.visibility = View.GONE
        binding.historyFragmentPageLayout.visibility = View.VISIBLE

        binding.historyFragmentPreviousPageButton.isEnabled = budgetDataApi.page.value!! > 1
        binding.historyFragmentFilterListButton.isEnabled = true
        binding.historyFragmentNextPageButton.isEnabled = !budgetDataApi.isLastPage.value!!
    }
    //endregion
}