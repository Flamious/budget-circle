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
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentHistoryChartBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.settings.charts.MultipleBarChartSetter
import com.example.budgetcircle.viewmodel.BudgetCircleData
import com.example.budgetcircle.viewmodel.models.ChartOperation

class HistoryChartFragment : Fragment() {
    lateinit var binding: FragmentHistoryChartBinding
    private lateinit var periods: Array<String>
    private var periodIndex: Int = 0

    private val budgetCircleData: BudgetCircleData by activityViewModels()

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
        periodIndex = periods.indexOf(budgetCircleData.chartOperationPeriod.value!!)

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

        val colors = if (Settings.isDay())
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
                if (Settings.isDay()) R.color.text_primary else R.color.light_grey
            ),
            xToBottom = false,
        )
    }

    private fun setTheme() {
        if (Settings.isNight()) {
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
                    budgetCircleData.chartOperationPeriod.postValue(periods[periodIndex])
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
                    budgetCircleData.chartOperationPeriod.postValue(periods[periodIndex])
                }

                binding.historyChartFragmentPreviousPeriodButton.visibility = if (periodIndex > 0) View.VISIBLE else View.INVISIBLE
                visibility = if (periodIndex < periods.size - 1) View.VISIBLE else View.INVISIBLE
            }
        }

        binding.historyChartFragmentChooseBudgetTypeButton.setOnClickListener {
            val types =
                Array(budgetCircleData.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetCircleData.budgetTypes.value!![index - 1].title else resources.getString(
                        R.string.all
                    )
                }
            val typesId =
                Array(budgetCircleData.budgetTypes.value!!.size + 1) { index ->
                    if (index > 0) budgetCircleData.budgetTypes.value!![index - 1].id else 0
                }

            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.budgetTypes),
                types,
                typesId,
                budgetCircleData.operationChartChosenBudgetTypeString,
                budgetCircleData.operationChartChosenBudgetType,
                if (Settings.isDay()) R.style.orangeEdgeEffect else R.style.darkEdgeEffect
            )
        }
    }

    private fun setObservation() {
        budgetCircleData.chartOperationPeriod.observe(this.viewLifecycleOwner) {
            binding.historyChartFragmentPeriod.text = it

            budgetCircleData.getChartOperations()
        }

        budgetCircleData.operationChartChosenBudgetType.observe(this.viewLifecycleOwner) {
            budgetCircleData.getChartOperations()
        }

        budgetCircleData.chartOperations.observe(this.viewLifecycleOwner) {
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