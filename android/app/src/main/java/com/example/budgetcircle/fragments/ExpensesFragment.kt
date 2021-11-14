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
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.forms.ExpensesFormActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.main.OperationSum
import com.example.budgetcircle.databinding.FragmentExpensesBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetData
import kotlin.collections.ArrayList

class ExpensesFragment : Fragment() {
    lateinit var binding: FragmentExpensesBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()

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
                budgetData.expensesDateString,
                budgetData.expensesDate
            )
        }
    }

    private fun setChart(expenseSums: List<OperationSum>) {
        val values = Array(expenseSums.size) { index -> expenseSums[index].sum }
        val titles =
            Array(expenseSums.size) { index -> if (MainActivity.isRu()) expenseSums[index].titleRu else expenseSums[index].title }
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
                    budgetData.addExpense(
                        expenseTitle,
                        result.data?.getDoubleExtra("sum", 0.0)!!,
                        budgetData.expenseTypes[expenseTypeIndex].id,
                        budgetData.budgetTypes.value!![budgetTypeIndex].id,
                        expenseCommentary
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
        budgetData.expensesDateString.observe(this.viewLifecycleOwner, {
            binding.periodText.text = it
        })
        budgetData.expenseSumByDate.observe(this.viewLifecycleOwner, {
            setChart(it)
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
        if (MainActivity.isRu()) {
            intent.putExtra(
                "budgetTypes",
                Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].titleRu })

            intent.putExtra(
                "expenseTypes",
                Array(budgetData.expenseTypes.size) { index -> budgetData.expenseTypes[index].titleRu })
        } else {
            intent.putExtra(
                "budgetTypes",
                Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].title })

            intent.putExtra(
                "expenseTypes",
                Array(budgetData.expenseTypes.size) { index -> budgetData.expenseTypes[index].title })
        }
        intent.putExtra(
            "budgetTypesSums",
            Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].sum })
        launcher?.launch(intent)
    }
    //endregion
}