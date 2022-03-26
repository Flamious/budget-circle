package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.forms.ExpensesFormActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentExpensesBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.models.Operation
import com.example.budgetcircle.viewmodel.models.OperationSum
import kotlin.collections.ArrayList

class ExpensesFragment : Fragment() {
    lateinit var binding: FragmentExpensesBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetDataApi: BudgetDataApi by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpensesBinding.inflate(inflater)
        setButtons()
        setObservation()
        setLauncher()
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeOrientation()
    }

    //region Setting
    private fun setButtons() {
        binding.addExpenseButton.setOnClickListener {
            addExpense()
        }
        binding.periodText.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.choosingPeriod),
                resources.getStringArray(R.array.periodsString),
                resources.getIntArray(R.array.periodsInt).toTypedArray(),
                budgetDataApi.expensesDateString,
                budgetDataApi.expensesDate
            )
        }
    }

    private fun setChart(expenseSums: List<OperationSum>) {
        val values = Array(expenseSums.size) { index -> expenseSums[index].sum }
        val titles = Array(expenseSums.size) { index -> expenseSums[index].type }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors = resources.getIntArray(R.array.expense_colors).toCollection(ArrayList())
        if (sum > 0)
            PieChartSetter.setChart(
                titles,
                values,
                colors,
                sum,
                resources.getString(R.string.total),
                binding.expensesPieChart,
                binding.sumText,
                binding.kindText,
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
        else
            PieChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(100.0),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                sum,
                resources.getString(R.string.no_entries),
                binding.expensesPieChart,
                binding.sumText,
                binding.kindText,
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
                true
            )
    }

    private fun setLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val budgetTypeIndex = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                    val expenseTypeIndex = result.data?.getIntExtra("typeIndex", 0)!!
                    val expenseTitle = result.data?.getStringExtra("title")!!
                    val expenseCommentary = result.data?.getStringExtra("commentary")!!
                    budgetDataApi.addOperation(
                        Operation(
                            -1,
                            expenseTitle,
                            result.data?.getDoubleExtra("sum", 0.0)!!,
                            "",
                            budgetDataApi.expenseTypes.value!![expenseTypeIndex].id,
                            budgetDataApi.budgetTypes.value!![budgetTypeIndex].id,
                            expenseCommentary,
                            true
                        )
                    )
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.added),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setObservation() {
        budgetDataApi.expensesDateString.observe(this.viewLifecycleOwner, {
            binding.periodText.text = it
        })
        budgetDataApi.expensesDate.observe(this.viewLifecycleOwner, {
            budgetDataApi.getExpenseSums(it)
        })
        budgetDataApi.expenseSums.observe(this.viewLifecycleOwner, {
            if (it != null) {
                setChart(it)
            }
        })
    }

    //endregion
    //region Methods
    private fun changeOrientation() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, ExpensesFragment())
            ?.commit()
    }

    private fun addExpense() {
        val intent = Intent(activity, ExpensesFormActivity::class.java)
        intent.putExtra(
            "budgetTypes",
            Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].title })
        intent.putExtra(
            "budgetTypesSums",
            Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].sum })
        intent.putExtra(
            "expenseTypes",
            Array(budgetDataApi.expenseTypes.value!!.size) { index -> budgetDataApi.expenseTypes.value!![index].title })
        launcher?.launch(intent)
    }
    //endregion
}