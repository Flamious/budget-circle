package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentOperationBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.OperationFormActivity
import com.example.budgetcircle.lists.OperationTypeListFragment
import com.example.budgetcircle.settings.charts.BarChartSetter
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.settings.charts.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetCircleData
import com.example.budgetcircle.viewmodel.models.Operation
import com.example.budgetcircle.viewmodel.models.OperationSum
import com.example.budgetcircle.viewmodel.models.ScheduledOperation

class OperationFragment(val isExpense: Boolean) : Fragment() {
    lateinit var binding: FragmentOperationBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetCircleData: BudgetCircleData by activityViewModels()
    private var isPieChart = true

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
        binding = FragmentOperationBinding.inflate(inflater)
        setButtons()
        setTheme()
        setObservation()
        setLauncher()
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeOrientation()
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    //region Setting
    private fun setButtons() {
        binding.operationFragmentListButton.setOnClickListener {
            openTypeList()
        }
        binding.operationFragmentAddButton.setOnClickListener {
            addOperation()
        }
        binding.operationFragmentChangeChartButton.setOnClickListener {
            changeChart()
        }
        binding.operationFragmentChangePeriodButton.setOnClickListener {
            choosePeriod()
        }
    }

    private fun setTheme() {
        val textColor: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (Settings.isDay()) {
                textColor = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    R.color.text_primary
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    R.color.text_secondary
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    R.color.light_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    if (isExpense) R.color.red_main else R.color.blue_main
                )
            } else {
                textColor = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    R.color.darker_grey
                )
            }

            operationFragmentTitle.text =
                resources.getText(if (isExpense) R.string.expenses_fragment else R.string.earnings_fragment)

            operationFragmentChangeChartButton.backgroundTintList =
                ColorStateList.valueOf(mainColor)
            operationFragmentHeaderLayout.setBackgroundColor(mainColor)
            operationFragmentChangePeriodButton.backgroundTintList =
                ColorStateList.valueOf(mainColor)
            operationFragmentChangePeriodButton.backgroundTintList =
                ColorStateList.valueOf(mainColor)
            operationFragmentAddButton.backgroundTintList = ColorStateList.valueOf(mainColor)
            operationFragmentLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            operationFragmentListButton.backgroundTintList = ColorStateList.valueOf(mainColor)

            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                operationFragmentPeriodText.setTextColor(textColor)
                operationFragmentKindText.setTextColor(textColor)
                operationFragmentPeriodTitle?.setTextColor(textSecondary)
                operationFragmentSumTitle?.setTextColor(textSecondary)
                operationFragmentKindTitle?.setTextColor(textSecondary)
            } else {
                operationFragmentPeriodText.setTextColor(textSecondary)
                operationFragmentKindText.setTextColor(textSecondary)
            }

            operationFragmentSumText.setTextColor(textColor)

        }
    }

    private fun setBarChart(budgetTypes: List<OperationSum>) {
        val values = Array(budgetTypes.size) { index -> budgetTypes[index].sum }
        val titles = Array(budgetTypes.size) { index -> budgetTypes[index].type }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors = if (Settings.isDay())
            resources.getIntArray(if (isExpense) R.array.expense_colors else R.array.earning_colors)
                .toCollection(ArrayList())
        else
            resources.getIntArray(R.array.dark_colors).toCollection(ArrayList())

        if (sum > 0)
            BarChartSetter.setChart(
                titles,
                values,
                colors,
                binding.operationFragmentBarChart,
                ContextCompat.getColor(
                    this.requireContext(),
                    if (Settings.isDay()) R.color.text_primary else R.color.light_grey
                )
            )
        else
            BarChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(0.0),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                binding.operationFragmentBarChart,
                ContextCompat.getColor(
                    this.requireContext(),
                    if (Settings.isDay()) R.color.text_primary else R.color.light_grey
                ),
                true
            )
    }

    private fun setPieChart(sums: List<OperationSum>) {
        val values = Array(sums.size) { index -> sums[index].sum }
        val titles = Array(sums.size) { index -> sums[index].type }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors = if (Settings.isDay())
            resources.getIntArray(if (isExpense) R.array.expense_colors else R.array.earning_colors)
                .toCollection(ArrayList())
        else
            resources.getIntArray(R.array.dark_colors).toCollection(ArrayList())

        val holeColor = ContextCompat.getColor(
            this.requireContext(),
            if (Settings.isDay()) R.color.light_grey else R.color.dark_grey
        )

        if (sum > 0)
            PieChartSetter.setChart(
                titles,
                values,
                colors,
                sum,
                resources.getString(R.string.total),
                binding.operationFragmentPieChart,
                binding.operationFragmentSumText,
                binding.operationFragmentKindText,
                holeColor,
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
        else
            PieChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(100.0),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                sum,
                resources.getString(R.string.no_entries),
                binding.operationFragmentPieChart,
                binding.operationFragmentSumText,
                binding.operationFragmentKindText,
                holeColor,
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
                true
            )
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val budgetTypeIndex = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                    val typeIndex = result.data?.getIntExtra("typeIndex", 0)!!
                    val title = result.data?.getStringExtra("title")!!
                    val commentary = result.data?.getStringExtra("commentary")!!

                    val isScheduled = result.data?.getBooleanExtra("isScheduled", false)!!
                    if (isScheduled) {
                        budgetCircleData.addScheduledOperation(
                            ScheduledOperation(
                                -1,
                                title,
                                result.data?.getDoubleExtra("sum", 0.0)!!,
                                if (isExpense) budgetCircleData.expenseTypes.value!![typeIndex].id else budgetCircleData.earningTypes.value!![typeIndex].id,
                                budgetCircleData.budgetTypes.value!![budgetTypeIndex].id,
                                commentary,
                                isExpense
                            )
                        )
                    } else {
                        budgetCircleData.addOperation(
                            Operation(
                                -1,
                                title,
                                result.data?.getDoubleExtra("sum", 0.0)!!,
                                "",
                                if (isExpense) budgetCircleData.expenseTypes.value!![typeIndex].id else budgetCircleData.earningTypes.value!![typeIndex].id,
                                budgetCircleData.budgetTypes.value!![budgetTypeIndex].id,
                                commentary,
                                isExpense
                            )
                        )
                    }
                }
            }
    }

    private fun setObservation() {
        if (isExpense) {
            budgetCircleData.expensesDateString.observe(this.viewLifecycleOwner, {
                binding.operationFragmentPeriodText.text = it
            })
            budgetCircleData.expensesDate.observe(this.viewLifecycleOwner, {
                budgetCircleData.getExpenseSums(it)
            })
            budgetCircleData.expenseSums.observe(this.viewLifecycleOwner, {
                if (it != null) {
                    if (isPieChart)
                        setPieChart(it)
                    else
                        setBarChart(it)
                }
            })
        } else {
            budgetCircleData.earningsDateString.observe(this.viewLifecycleOwner, {
                binding.operationFragmentPeriodText.text = it
            })
            budgetCircleData.earningsDate.observe(this.viewLifecycleOwner, {
                budgetCircleData.getEarningSums(it)
            })
            budgetCircleData.earningSums.observe(this.viewLifecycleOwner, {
                if (it != null) {
                    if (isPieChart)
                        setPieChart(it)
                    else
                        setBarChart(it)
                }
            })
        }
    }

    //endregion
    //region Methods
    private fun changeChart() {
        binding.apply {
            if (isPieChart) {
                operationFragmentPieChart.visibility = View.INVISIBLE
                operationFragmentBarChart.visibility = View.VISIBLE
                operationFragmentInfoLayout.visibility = View.INVISIBLE

                operationFragmentChangeChartButton.setImageResource(R.drawable.ic_pie_chart)
                setBarChart(if (isExpense) budgetCircleData.expenseSums.value!! else budgetCircleData.earningSums.value!!)
            } else {
                operationFragmentBarChart.visibility = View.INVISIBLE
                operationFragmentPieChart.visibility = View.VISIBLE
                operationFragmentInfoLayout.visibility = View.VISIBLE

                operationFragmentChangeChartButton.setImageResource(R.drawable.ic_bar_chart)
                setPieChart(if (isExpense) budgetCircleData.expenseSums.value!! else budgetCircleData.earningSums.value!!)
            }
            isPieChart = !isPieChart
        }
    }

    private fun appear() {
        binding.apply {
            operationFragmentTitle.startAnimation(appear)
            operationFragmentSumText.startAnimation(appear)
            operationFragmentKindText.startAnimation(appear)
            operationFragmentAddButton.startAnimation(appear)
            operationFragmentListButton.startAnimation(appear)
            operationFragmentPeriodText.startAnimation(appear)
            operationFragmentKindTitle?.startAnimation(appear)
            operationFragmentPeriodTitle?.startAnimation(appear)
            operationFragmentSumTitle?.startAnimation(appear)
        }
    }

    private fun changeOrientation() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, OperationFragment(isExpense))
            ?.commit()
    }

    private fun addOperation() {
        val intent = Intent(activity, OperationFormActivity::class.java)
        intent.putExtra("isExpense", isExpense)
        intent.putExtra(
            "budgetTypes",
            Array(budgetCircleData.budgetTypes.value!!.size) { index -> budgetCircleData.budgetTypes.value!![index].title })
        if (isExpense) {
            intent.putExtra(
                "types",
                Array(budgetCircleData.expenseTypes.value!!.size) { index -> budgetCircleData.expenseTypes.value!![index].title })
            intent.putExtra(
                "budgetTypesSums",
                Array(budgetCircleData.budgetTypes.value!!.size) { index -> budgetCircleData.budgetTypes.value!![index].sum })
        } else {
            intent.putExtra(
                "types",
                Array(budgetCircleData.earningTypes.value!!.size) { index -> budgetCircleData.earningTypes.value!![index].title })
        }
        launcher?.launch(intent)
    }

    private fun openTypeList() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, OperationTypeListFragment(isExpense))
            ?.commit()
    }

    private fun choosePeriod() {
        val effectColor: Int = if (Settings.isDay()) {
            if (isExpense) R.style.redEdgeEffect else R.style.blueEdgeEffect
        } else {
            R.style.darkEdgeEffect
        }

        Dialogs().chooseOne(
            this.requireContext(),
            resources.getString(R.string.choosingPeriod),
            resources.getStringArray(R.array.periodsString),
            resources.getIntArray(R.array.periodsInt).toTypedArray(),
            if (isExpense) budgetCircleData.expensesDateString else budgetCircleData.earningsDateString,
            if (isExpense) budgetCircleData.expensesDate else budgetCircleData.earningsDate,
            effectColor
        )
    }
    //endregion
}