package com.example.budgetcircle.fragments.history

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentHistoryChartBinding
import com.example.budgetcircle.databinding.FragmentPlannedBudgetBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.forms.PlannedBudgetActivity
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.settings.charts.BarChartSetter
import com.example.budgetcircle.settings.charts.MultipleBarChartSetter
import com.example.budgetcircle.viewmodel.BudgetCircleData
import com.example.budgetcircle.viewmodel.items.HistoryAdapter
import com.example.budgetcircle.viewmodel.items.OperationTypePartAdapter
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.example.budgetcircle.viewmodel.models.ChartOperation
import com.example.budgetcircle.viewmodel.models.OperationTypePart
import com.example.budgetcircle.viewmodel.models.PlannedBudgetModel
import com.github.mikephil.charting.charts.BarChart
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class PlannedBudgetFragment : Fragment() {
    lateinit var binding: FragmentPlannedBudgetBinding
    private lateinit var earningsAdapter: OperationTypePartAdapter
    private lateinit var expensesAdapter: OperationTypePartAdapter
    private lateinit var accountsAdapter: OperationTypePartAdapter
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var month: Int = -1
    private var year: Int = -1

    private var entityId: Int = -1
    private var plannedEarnings: Double = -1.0
    private var plannedExpenses: Double = -1.0

    private val budgetCircleData: BudgetCircleData by activityViewModels()

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_short_anim
        )
    }

    private val list_appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_short_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlannedBudgetBinding.inflate(inflater)

        setAdapter()
        setButtons()
        setTheme()
        setLauncher()
        setObservation()
        setInitialValues()
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
        val expenseColor: Int
        val earningColor: Int
        val accountColor: Int

        if (Settings.isDay()) {
            textPrimary = ContextCompat.getColor(this.requireContext(), R.color.text_primary)
            textSecondary = ContextCompat.getColor(this.requireContext(), R.color.text_secondary)
            backgroundColor = ContextCompat.getColor(this.requireContext(), R.color.white)
            borderColor = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
            expenseColor = ContextCompat.getColor(this.requireContext(), R.color.red_main)
            earningColor = ContextCompat.getColor(this.requireContext(), R.color.blue_main)
            accountColor = ContextCompat.getColor(this.requireContext(), R.color.green_main)
        } else {
            textPrimary = ContextCompat.getColor(this.requireContext(), R.color.light_grey)
            textSecondary = ContextCompat.getColor(this.requireContext(), R.color.grey)
            backgroundColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
            borderColor = ContextCompat.getColor(this.requireContext(), R.color.dark_grey)
            expenseColor = ContextCompat.getColor(this.requireContext(), R.color.red_secondary)
            earningColor = ContextCompat.getColor(this.requireContext(), R.color.blue_secondary)
            accountColor = ContextCompat.getColor(this.requireContext(), R.color.green_secondary)
        }

        earningsAdapter = OperationTypePartAdapter(
            textPrimary,
            textSecondary,
            backgroundColor,
            borderColor,
            earningColor
        )

        expensesAdapter = OperationTypePartAdapter(
            textPrimary,
            textSecondary,
            backgroundColor,
            borderColor,
            expenseColor
        )

        accountsAdapter = OperationTypePartAdapter(
            textPrimary,
            textSecondary,
            backgroundColor,
            borderColor,
            accountColor,expenseColor
        )

        binding.apply {
            plannedBudgetFragmentEarningsList.layoutManager =
                GridLayoutManager(this@PlannedBudgetFragment.context, 1)
            plannedBudgetFragmentEarningsList.adapter = earningsAdapter

            plannedBudgetFragmentExpensesList.layoutManager =
                GridLayoutManager(this@PlannedBudgetFragment.context, 1)
            plannedBudgetFragmentExpensesList.adapter = expensesAdapter

            plannedBudgetFragmentAccountsList.layoutManager =
                GridLayoutManager(this@PlannedBudgetFragment.context, 1)
            plannedBudgetFragmentAccountsList.adapter = accountsAdapter
        }
    }

    private fun setInitialValues() {
        month = Calendar.getInstance().get(Calendar.MONTH) + 1
        year = Calendar.getInstance().get(Calendar.YEAR)

        setPeriod(month, year)
    }

    private fun setPeriod(month: Int, year: Int) {
        binding.plannedBudgetFragmentPeriod.text = getTitle()

        startLoading()
        budgetCircleData.getPlannedBudget(month, year)
    }

    private fun setTheme() {
        if (Settings.isNight()) {
            binding.apply {
                val textPrimary = ContextCompat.getColor(
                    this@PlannedBudgetFragment.requireContext(),
                    R.color.light_grey
                )
                val backgroundColor = ContextCompat.getColor(
                    this@PlannedBudgetFragment.requireContext(),
                    R.color.dark_grey
                )
                val mainColor = ContextCompat.getColor(
                    this@PlannedBudgetFragment.requireContext(),
                    R.color.darker_grey
                )

                plannedBudgetFragmentHeaderLayout.setBackgroundColor(mainColor)
                plannedBudgetFragmentEditButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                plannedBudgetFragmentDeleteButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                plannedBudgetFragmentAddButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                plannedBudgetFragmentBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                plannedBudgetFragmentNextPeriodButton.backgroundTintList =
                    ColorStateList.valueOf(backgroundColor)
                plannedBudgetFragmentNextPeriodButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                plannedBudgetFragmentPreviousPeriodButton.backgroundTintList =
                    ColorStateList.valueOf(backgroundColor)
                plannedBudgetFragmentPreviousPeriodButton.imageTintList =
                    ColorStateList.valueOf(textPrimary)
                plannedBudgetFragmentEarningsLayout.setBackgroundColor(mainColor)
                plannedBudgetFragmentExpensesLayout.setBackgroundColor(mainColor)
                plannedBudgetFragmentAccountsLayout.setBackgroundColor(mainColor)
                plannedBudgetFragmentLayoutPeriodBorder.setBackgroundColor(mainColor)
                plannedBudgetFragmentPeriodLayout.setBackgroundColor(backgroundColor)
                plannedBudgetFragmentPeriod.setTextColor(textPrimary)
                plannedBudgetFragmentLayout.setBackgroundColor(backgroundColor)

                plannedBudgetFragmentEarningsTitle.setTextColor(textPrimary)
                plannedBudgetFragmentExpensesTitle.setTextColor(textPrimary)
                plannedBudgetFragmentAccountsTitle.setTextColor(textPrimary)

                plannedBudgetFragmentEarningsRatio.setTextColor(textPrimary)
                plannedBudgetFragmentExpensesRatio.setTextColor(textPrimary)
                plannedBudgetFragmentAccountsRatio.setTextColor(textPrimary)

                plannedBudgetFragmentProgressBar.indeterminateTintList =
                    ColorStateList.valueOf(mainColor)
            }
        }
    }

    private fun setBarChart(current: Double, planned: Double, resColors: Int, chart: BarChart) {
        val values = arrayOf(current, planned)
        val titles =
            arrayOf(resources.getString(R.string.current), resources.getString(R.string.planned))
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors = if (Settings.isDay())
            resources.getIntArray(resColors)
                .toCollection(ArrayList())
        else
            resources.getIntArray(R.array.dark_colors).toCollection(ArrayList())
        if (sum > 0)
            BarChartSetter.setChart(
                titles,
                values,
                colors,
                chart,
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
                chart,
                ContextCompat.getColor(
                    this.requireContext(),
                    if (Settings.isDay()) R.color.text_primary else R.color.light_grey
                ),
                true
            )
    }

    private fun setButtons() {
        binding.plannedBudgetFragmentBackButton.setOnClickListener {
            exit()
        }
        binding.plannedBudgetFragmentPreviousPeriodButton.setOnClickListener {
            calculatePeriod(-1)
        }
        binding.plannedBudgetFragmentNextPeriodButton.setOnClickListener {
            calculatePeriod(1)
        }
        binding.plannedBudgetFragmentAddButton.setOnClickListener {
            addPlannedBudget()
        }
        binding.plannedBudgetFragmentEditButton.setOnClickListener {
            editPlannedBudget()
        }
        binding.plannedBudgetFragmentDeleteButton.setOnClickListener {

            val background: Int
            val buttonColor: Int

            if (Settings.isNight()) {
                background = R.style.darkEdgeEffect
                buttonColor = ContextCompat.getColor(this.requireContext(), R.color.darker_grey)
            } else {
                background = R.style.orangeEdgeEffect
                buttonColor = ContextCompat.getColor(this.requireContext(), R.color.orange_main)
            }

            Dialogs().chooseYesNo(
                this.requireContext(),
                resources.getString(R.string.delete) + " " + getTitle(),
                resources.getString(R.string.r_u_sure),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                buttonColor,
                ::deletePlannedBudget,
                background
            )
        }
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val plannedEarnings = result.data?.getDoubleExtra("plannedEarnings", 0.0)!!
                    val plannedExpenses = result.data?.getDoubleExtra("plannedExpenses", 0.0)!!

                    if (result.data?.getStringExtra("type")!! == "new") {
                        budgetCircleData.addPlannedBudget(
                            PlannedBudgetModel(
                                month,
                                year,
                                plannedEarnings,
                                plannedExpenses
                            )
                        )
                    } else {
                        budgetCircleData.editPlannedBudget(
                            entityId,
                            PlannedBudgetModel(
                                month,
                                year,
                                plannedEarnings,
                                plannedExpenses
                            )
                        )
                    }
                }
            }

    }

    private fun setObservation() {
        budgetCircleData.plannedBudget.observe(this.viewLifecycleOwner) {
            binding.apply {
                if (it != null) {
                    if (it.isPlanned) {
                        val earningRatio = "${it.currentEarnings}/${it.plannedEarnings}"
                        val expenseRatio = "${it.currentExpenses}/${it.plannedExpenses}"
                        val accountsRatio = "${it.currentBudget}/${it.plannedBudget}"

                        plannedBudgetFragmentEarningsRatio.text = earningRatio
                        plannedBudgetFragmentExpensesRatio.text = expenseRatio
                        plannedBudgetFragmentAccountsRatio.text = accountsRatio

                        earningsAdapter.setList(it.earnings)
                        expensesAdapter.setList(it.expenses)
                        accountsAdapter.setList(it.accounts)

                        setBarChart(it.currentEarnings, it.plannedEarnings, R.array.earning_colors, plannedBudgetFragmentEarningsBarChart)
                        setBarChart(it.currentExpenses, it.plannedExpenses, R.array.expense_colors, plannedBudgetFragmentExpensesBarChart)
                        setBarChart(it.currentBudget, it.plannedBudget, R.array.budget_colors, plannedBudgetFragmentAccountsBarChart)

                        entityId = it.id
                        plannedEarnings = it.plannedEarnings
                        plannedExpenses = it.plannedExpenses
                        plannedExpenses = it.plannedExpenses

                        stopLoading(false)
                    } else {
                        stopLoading(true)
                    }
                }
            }
        }
    }

    //endregion
    //region Methods
    private fun startLoading() {
        binding.plannedBudgetFragmentProgressBar.visibility = View.VISIBLE
        binding.plannedBudgetFragmentScrollView.visibility = View.INVISIBLE
        binding.plannedBudgetFragmentAddButton.visibility = View.GONE

        binding.plannedBudgetFragmentNextPeriodButton.isEnabled = false
        binding.plannedBudgetFragmentPreviousPeriodButton.isEnabled = false
        binding.plannedBudgetFragmentEditButton.isEnabled = false
        binding.plannedBudgetFragmentDeleteButton.isEnabled = false
    }

    private fun stopLoading(isEmpty: Boolean = false) {
        if (!isEmpty) {
            binding.plannedBudgetFragmentScrollView.visibility = View.VISIBLE
            binding.plannedBudgetFragmentEditButton.visibility = View.VISIBLE
            binding.plannedBudgetFragmentDeleteButton.visibility = View.VISIBLE
            createChartLayouts()
        } else {
            binding.plannedBudgetFragmentAddButton.visibility = View.VISIBLE
            binding.plannedBudgetFragmentEditButton.visibility = View.GONE
            binding.plannedBudgetFragmentDeleteButton.visibility = View.GONE
        }
        binding.plannedBudgetFragmentProgressBar.visibility = View.GONE

        binding.plannedBudgetFragmentNextPeriodButton.isEnabled = true
        binding.plannedBudgetFragmentPreviousPeriodButton.isEnabled = true
        binding.plannedBudgetFragmentEditButton.isEnabled = true
        binding.plannedBudgetFragmentDeleteButton.isEnabled = true
    }

    private fun createChartLayouts() {
        binding.plannedBudgetFragmentExpensesLayout.startAnimation(list_appear)
        binding.plannedBudgetFragmentEarningsLayout.startAnimation(list_appear)
        binding.plannedBudgetFragmentAccountsLayout.startAnimation(list_appear)
    }

    private fun appear() {
        binding.plannedBudgetFragmentHeaderLayout.startAnimation(appear)
        createChartLayouts()
    }

    private fun addPlannedBudget() {
        val intent = Intent(activity, PlannedBudgetActivity::class.java)
        intent.putExtra("date", getTitle())
        launcher?.launch(intent)
    }

    private fun editPlannedBudget() {
        val intent = Intent(activity, PlannedBudgetActivity::class.java)
        intent.putExtra("date", getTitle())
        intent.putExtra("edit", "edit")
        intent.putExtra("plannedEarnings", plannedEarnings)
        intent.putExtra("plannedExpenses", plannedExpenses)
        launcher?.launch(intent)
    }

    private fun deletePlannedBudget() {
        budgetCircleData.deletePlannedBudget(entityId, month, year)
    }

    private fun calculatePeriod(direction: Int) {
        month += direction

        if (month == 0) {
            year--
            month = 12
        }

        if (month == 13) {
            year++
            month = 1
        }

        if (year == 2018 && month == 1) {
            binding.plannedBudgetFragmentPreviousPeriodButton.visibility = View.INVISIBLE
        } else {
            binding.plannedBudgetFragmentPreviousPeriodButton.visibility = View.VISIBLE
        }

        if (year == 2099 && month == 12) {
            binding.plannedBudgetFragmentNextPeriodButton.visibility = View.INVISIBLE
        } else {
            binding.plannedBudgetFragmentNextPeriodButton.visibility = View.VISIBLE
        }

        setPeriod(month, year)
    }

    private fun exit() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, HistoryChartFragment())
            ?.commit()
    }

    private fun getTitle(): String {
        return "${if (month < 10) "0$month" else month}.$year"
    }
    //endregion
}