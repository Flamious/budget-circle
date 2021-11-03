package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
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
import androidx.fragment.app.viewModels
import com.example.budgetcircle.forms.ExpensesFormActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.main.OperationSum
import com.example.budgetcircle.databinding.FragmentExpensesBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExpensesFragment : Fragment() {
    lateinit var binding: FragmentExpensesBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        budgetData.expensesDateString.observe(this, {
            binding.periodText.text = it
        })
        budgetData.expensesSum.observe(this, {
            binding.sumText.text = "%.2f".format(it)
        })
        budgetData.expenseSumByDate.observe(this, {
            setChart(it)
        })
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val budgetTypeIndex = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                    val expenseTypeIndex = result.data?.getIntExtra("expenseTypeIndex", 0)!!
                    budgetData.addExpense(
                        result.data?.getFloatExtra("sum", 0f)!!,
                        budgetData.expenseTypes[expenseTypeIndex].id,
                        budgetData.budgetTypes.value!![budgetTypeIndex].id
                    )
                    /*budgetData.addExpense(result.data?.getFloatExtra("sum", 0f))

                    budgetData.addToOperationList(
                        HistoryItem(
                        1,
                        result.data?.getFloatExtra("sum", 0f)!!,
                        result.data?.getStringExtra("title")!!,
                        result.data?.getStringExtra("type")!!,
                        SimpleDateFormat("dd.MM.yyyy").parse(result.data?.getStringExtra("date")!!),
                        resources.getColor(R.color.red_button),
                        result.data?.getBooleanExtra("isRep", false)!!))*/

                    Toast.makeText(
                        activity,
                        "Added",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpensesBinding.inflate(inflater)
        setButtons()
        return binding.root
    }

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
        val titles = Array(expenseSums.size) { index -> expenseSums[index].title }
        var sum = 0f
        for (n in values) {
            sum += n
        }
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
                binding.kindText
            )
        else
            PieChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(100f),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                sum,
                resources.getString(R.string.no_entries),
                binding.expensesPieChart,
                binding.sumText,
                binding.kindText,
                true
            )
    }

    private fun addExpense() {
        val intent = Intent(activity, ExpensesFormActivity::class.java)
        intent.putExtra(
            "budgetTypes",
            Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].title })
        intent.putExtra(
            "expenseTypes",
            Array(budgetData.expenseTypes.size) { index -> budgetData.expenseTypes[index].title })
        launcher?.launch(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExpensesFragment()
    }
}