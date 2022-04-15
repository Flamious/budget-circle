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
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentHistoryChartBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.settings.BarChartSetter
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.MultipleBarChartSetter
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.example.budgetcircle.viewmodel.models.ChartOperation

class HistoryChartFragment : Fragment() {
    lateinit var binding: FragmentHistoryChartBinding
    private lateinit var periods: Array<String>
    private var periodIndex: Int = 0

    private val budgetDataApi: BudgetDataApi by activityViewModels()

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_short_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryChartBinding.inflate(inflater)
        periods = resources.getStringArray(R.array.chartPeriodsString)
        periodIndex = periods.indexOf(budgetDataApi.chartOperationPeriod.value!!)

        setButtons()
        setObservation()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setBarChart(operations: List<ChartOperation>) {
        val values1 = Array(operations.size) { index -> operations[index].expenses }
        val values2 = Array(operations.size) { index -> operations[index].earnings }
        val values3 = Array(operations.size) { index -> operations[index].exchanges }
        val titles = Array(operations.size) { index -> operations[index].title }
        val colors = resources.getIntArray(R.array.history_chart_colors).toCollection(ArrayList())

        MultipleBarChartSetter.setChart(
            titles,
            values1,
            values2,
            values3,
            colors,
            binding.operationBarChart,
            xToBottom = false,
        )
    }

    private fun setButtons() {
        binding.historyChartBackButton.setOnClickListener {
            exit()
        }
        binding.previousPeriodButton.apply {
            isEnabled = periodIndex > 0

            setOnClickListener {
                if (periodIndex > 0) {
                    periodIndex--
                    budgetDataApi.chartOperationPeriod.postValue(periods[periodIndex])
                }

                isEnabled = periodIndex > 0
                binding.nextPeriodButton.isEnabled = periodIndex < periods.size - 1
            }
        }

        binding.nextPeriodButton.apply {
            isEnabled = periodIndex < periods.size - 1

            setOnClickListener {
                if (periodIndex < periods.size - 1) {
                    periodIndex++
                    budgetDataApi.chartOperationPeriod.postValue(periods[periodIndex])
                }

                binding.previousPeriodButton.isEnabled = periodIndex > 0
                isEnabled = periodIndex < periods.size - 1
            }
        }

        binding.chooseBudgetTypeButton.setOnClickListener {
            val types =
                Array(budgetDataApi.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetDataApi.budgetTypes.value!![index - 1].title else resources.getString(
                        R.string.all
                    )
                }
            val typesId =
                Array(budgetDataApi.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetDataApi.budgetTypes.value!![index - 1].id else 0
                }

            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.budgetTypes),
                types,
                typesId,
                budgetDataApi.operationChartChosenBudgetTypeString,
                budgetDataApi.operationChartChosenBudgetType,
                R.style.orangeEdgeEffect
            )
        }
    }

    private fun setObservation() {
        budgetDataApi.chartOperationPeriod.observe(this.viewLifecycleOwner) {
            binding.historyChartPeriod.text = it

            budgetDataApi.getChartOperations()
        }

        budgetDataApi.operationChartChosenBudgetType.observe(this.viewLifecycleOwner) {
            budgetDataApi.getChartOperations()
        }

        budgetDataApi.chartOperations.observe(this.viewLifecycleOwner) {
            if (it != null)
                setBarChart(it)
        }
    }

    //endregion
    //region Methods
    private fun appear() {
        binding.historyChartHeaderLayout.startAnimation(appear)
        binding.hitoryChartPeriodLayout.startAnimation(appear)
    }

    private fun exit() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryFragment())
            ?.commit()
    }
    //endregion
}