package com.example.budgetcircle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentExpensesBinding
import com.example.budgetcircle.settings.PieChartSetter

class ExpensesFragment : Fragment() {
    lateinit var binding: FragmentExpensesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpensesBinding.inflate(inflater)
        setChart()
        return binding.root
    }

    private fun setChart() {
        val values = arrayListOf(50f, 20f, 15f, 10f, 5f, 92f, 11f, 3f)
        val titles = resources.getStringArray(R.array.titles).toCollection(ArrayList())
        val colors = resources.getIntArray(R.array.expense_colors).toCollection(ArrayList())

        PieChartSetter.setChart(titles, values, colors, binding.expensesPieChart)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExpensesFragment()
    }
}