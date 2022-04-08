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
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.models.Operation
import com.example.budgetcircle.viewmodel.models.OperationSum

class OperationFragment(val isExpense: Boolean) : Fragment() {
    lateinit var binding: FragmentOperationBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
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
            addEarning()
        }
        binding.operationFragmentPeriodText.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.choosingPeriod),
                resources.getStringArray(R.array.periodsString),
                resources.getIntArray(R.array.periodsInt).toTypedArray(),
                if (isExpense) budgetDataApi.expensesDateString else budgetDataApi.earningsDateString,
                if (isExpense) budgetDataApi.expensesDate else budgetDataApi.earningsDate
            )
        }
    }

    private fun setTheme() {
        binding.apply {
            operationFragmentTitle.text =
                resources.getText(if (isExpense) R.string.expenses_fragment else R.string.earnings_fragment)
            val mainColor = if (isExpense) R.color.red_main else R.color.blue_main

            operationFragmentAddButton.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    mainColor
                )
            )

            operationFragmentListButton.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@OperationFragment.requireContext(),
                    mainColor
                )
            )
        }
    }

    private fun setChart(sums: List<OperationSum>) {
        val values = Array(sums.size) { index -> sums[index].sum }
        val titles = Array(sums.size) { index -> sums[index].type }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors =
            resources.getIntArray(if (isExpense) R.array.expense_colors else R.array.earning_colors)
                .toCollection(ArrayList())
        if (sum > 0)
            PieChartSetter.setChart(
                titles,
                values,
                colors,
                sum,
                resources.getString(R.string.total),
                binding.operationsPieChart,
                binding.operationFragmentSumText,
                binding.operationFragmentKindText,
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
        else
            PieChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(100.0),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                sum,
                resources.getString(R.string.no_entries),
                binding.operationsPieChart,
                binding.operationFragmentSumText,
                binding.operationFragmentKindText,
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
                    budgetDataApi.addOperation(
                        Operation(
                            -1,
                            title,
                            result.data?.getDoubleExtra("sum", 0.0)!!,
                            "",
                            if (isExpense) budgetDataApi.expenseTypes.value!![typeIndex].id else budgetDataApi.earningTypes.value!![typeIndex].id,
                            budgetDataApi.budgetTypes.value!![budgetTypeIndex].id,
                            commentary,
                            isExpense
                        )
                    )
                }
            }
    }

    private fun setObservation() {
        if (isExpense) {
            budgetDataApi.expensesDateString.observe(this.viewLifecycleOwner, {
                binding.operationFragmentPeriodText.text = it
            })
            budgetDataApi.expensesDate.observe(this.viewLifecycleOwner, {
                budgetDataApi.getExpenseSums(it)
            })
            budgetDataApi.expenseSums.observe(this.viewLifecycleOwner, {
                if (it != null) {
                    setChart(it)
                }
            })
        } else {
            budgetDataApi.earningsDateString.observe(this.viewLifecycleOwner, {
                binding.operationFragmentPeriodText.text = it
            })
            budgetDataApi.earningsDate.observe(this.viewLifecycleOwner, {
                budgetDataApi.getEarningSums(it)
            })
            budgetDataApi.earningSums.observe(this.viewLifecycleOwner, {
                if (it != null) {
                    setChart(it)
                }
            })
        }
    }

    //endregion
    //region Methods
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

    private fun addEarning() {
        val intent = Intent(activity, OperationFormActivity::class.java)
        intent.putExtra("isExpense", isExpense)
        intent.putExtra(
            "budgetTypes",
            Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].title })
        if (isExpense) {
            intent.putExtra(
                "types",
                Array(budgetDataApi.expenseTypes.value!!.size) { index -> budgetDataApi.expenseTypes.value!![index].title })
            intent.putExtra(
                "budgetTypesSums",
                Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].sum })
        } else {
            intent.putExtra(
                "types",
                Array(budgetDataApi.earningTypes.value!!.size) { index -> budgetDataApi.earningTypes.value!![index].title })
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
    //endregion
}