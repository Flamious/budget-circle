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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.budgetcircle.forms.ExpensesFormActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentExpensesBinding
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

        budgetData.expensesSum.observe(this, {
            binding.sumText.text = "%.2f".format(it)
        })

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val budgetTypeIndex = result.data?.getIntExtra("budgetTypeIndex", 0)!!
                    val expenseTypeIndex = result.data?.getIntExtra("isRep", 0)!!
                    /*budgetData.addEarning(result.data?.getFloatExtra("sum", 0f))*/
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
        setChart()
        setButtons()
        return binding.root
    }

    private fun setButtons() {
        binding.addExpenseButton.setOnClickListener {
            addExpense()
        }
    }

    private fun setChart() {
        /*val values = arrayOf(50f, 20f, 15f, 10f, 5f, 92f, 11f, 3f)
        var i = 0f
        for (n in values) {
            i += n
        }
        val titles = resources.getStringArray(R.array.expense_titles).toCollection(ArrayList())
        val colors = resources.getIntArray(R.array.expense_colors).toCollection(ArrayList())
        if (i > 0)
            PieChartSetter.setChart(titles.toTypedArray(), values, colors, binding.expensesPieChart)
        else
            PieChartSetter.setChart(
                arrayOf("No entries"), arrayOf(100f),
                arrayListOf(resources.getColor(R.color.no_money_op)), binding.expensesPieChart
            )*/
    }

    private fun addExpense() {
        val intent = Intent(activity, ExpensesFormActivity::class.java)
        intent.putExtra(
            "budgetTypes",
            Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].title })
        intent.putExtra(
            "expenseTypes",
            Array(budgetData.earningTypes.size) { index -> budgetData.earningTypes[index].title })
        launcher?.launch(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExpensesFragment()
    }
}