package com.example.budgetcircle.fragments.history

import android.content.res.ColorStateList
import android.content.res.Configuration
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
import com.example.budgetcircle.fragments.UserFragment
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
        setTheme()
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

        val colors = if (BudgetDataApi.mode.value!! == UserFragment.DAY)
            resources.getIntArray(R.array.history_chart_colors).toCollection(ArrayList())
        else
            resources.getIntArray(R.array.history_chart_colors_dark).toCollection(ArrayList())

        MultipleBarChartSetter.setChart(
            titles,
            values1,
            values2,
            values3,
            colors,
            binding.historyChartFragmentBarChart,
            ContextCompat.getColor(
                this.requireContext(),
                if (BudgetDataApi.mode.value!! == UserFragment.DAY) R.color.text_primary else R.color.light_grey
            ),
            xToBottom = false,
        )
    }

    private fun setTheme() {
        if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
            binding.apply {
                val textPrimary = ContextCompat.getColor(
                    this@HistoryChartFragment.requireContext(),
                    R.color.light_grey
                )
                val backgroundColor = ContextCompat.getColor(
                    this@HistoryChartFragment.requireContext(),
                    R.color.dark_grey
                )
                val mainColor = ContextCompat.getColor(
                    this@HistoryChartFragment.requireContext(),
                    R.color.darker_grey
                )

                historyChartFragmentHeaderLayout.setBackgroundColor(mainColor)
                historyChartFragmentChooseBudgetTypeButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                historyChartFragmentBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                historyChartFragmentNextPeriodButton.backgroundTintList =
                    ColorStateList.valueOf(backgroundColor)
                historyChartFragmentNextPeriodButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                historyChartFragmentPreviousPeriodButton.backgroundTintList =
                    ColorStateList.valueOf(backgroundColor)
                historyChartFragmentPreviousPeriodButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                historyChartFragmentPeriodLayout.setBackgroundColor(backgroundColor)
                historyChartFragmentPeriod.setTextColor(textPrimary)
                historyChartFragmentLayout.setBackgroundColor(backgroundColor)
            }
        }
    }

    private fun setButtons() {
        binding.historyChartFragmentBackButton.setOnClickListener {
            exit()
        }
        binding.historyChartFragmentPreviousPeriodButton.apply {
            visibility = if (periodIndex > 0) View.VISIBLE else View.INVISIBLE

            setOnClickListener {
                if (periodIndex > 0) {
                    periodIndex--
                    budgetDataApi.chartOperationPeriod.postValue(periods[periodIndex])
                }

                visibility = if (periodIndex > 0) View.VISIBLE else View.INVISIBLE
                binding.historyChartFragmentNextPeriodButton.visibility =
                    if (periodIndex < periods.size - 1) View.VISIBLE else View.INVISIBLE
            }
        }

        binding.historyChartFragmentNextPeriodButton.apply {
            visibility = if (periodIndex < periods.size - 1) View.VISIBLE else View.INVISIBLE

            setOnClickListener {
                if (periodIndex < periods.size - 1) {
                    periodIndex++
                    budgetDataApi.chartOperationPeriod.postValue(periods[periodIndex])
                }

                binding.historyChartFragmentPreviousPeriodButton.visibility = if (periodIndex > 0) View.VISIBLE else View.INVISIBLE
                visibility = if (periodIndex < periods.size - 1) View.VISIBLE else View.INVISIBLE
            }
        }

        binding.historyChartFragmentChooseBudgetTypeButton.setOnClickListener {
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
                if (BudgetDataApi.mode.value!! == UserFragment.DAY) R.style.orangeEdgeEffect else R.style.darkEdgeEffect
            )
        }
    }

    private fun setObservation() {
        budgetDataApi.chartOperationPeriod.observe(this.viewLifecycleOwner) {
            binding.historyChartFragmentPeriod.text = it

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
        binding.historyChartFragmentHeaderLayout.startAnimation(appear)
        binding.historyChartFragmentPeriodLayout.startAnimation(appear)
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