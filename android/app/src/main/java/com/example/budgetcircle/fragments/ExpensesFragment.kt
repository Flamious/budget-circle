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
import com.example.budgetcircle.forms.ExpensesFormActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentExpensesBinding
import com.example.budgetcircle.settings.PieChartSetter

class ExpensesFragment : Fragment() {
    lateinit var binding: FragmentExpensesBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(activity, result.data?.getStringExtra("Ha").toString(), Toast.LENGTH_LONG).show()
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
        binding.addButton.setOnClickListener() {
            addExpense()
        }
    }

    private fun setChart() {
        val values = arrayListOf(50f, 20f, 15f, 10f, 5f, 92f, 11f, 3f)
        /*val values = arrayListOf(0f, 0f, 0f)*/
        var i: Float = 0f
        for(n in values) {
          i += n
        }
        val titles = resources.getStringArray(R.array.expense_titles).toCollection(ArrayList())
        val colors = resources.getIntArray(R.array.expense_colors).toCollection(ArrayList())
        if (i > 0)
            PieChartSetter.setChart(titles, values, colors, binding.expensesPieChart)
        else
            PieChartSetter.setChart(arrayListOf("No entries"), arrayListOf(100f),
                arrayListOf(resources.getColor(R.color.no_money_op)), binding.expensesPieChart)
    }

    private fun addExpense() {
        val intent = Intent(activity, ExpensesFormActivity::class.java)
        launcher?.launch(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExpensesFragment()
    }
}