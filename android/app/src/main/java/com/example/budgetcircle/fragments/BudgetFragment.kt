package com.example.budgetcircle.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentBudgetBinding
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet




class BudgetFragment : Fragment() {
    lateinit var binding: FragmentBudgetBinding
    val budgetData: BudgetData by activityViewModels()
    var sliceSpace = 3f
    var holeRadius = 90f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetBinding.inflate(inflater)
        showPieChart()
        return binding.root
    }

    private fun showPieChart() {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = "type"

        val typeAmountMap = arrayListOf("Example1", "Example2", "Example3", "Example4", "Example5")
        val numberAmount = arrayListOf(50f, 20f, 15f, 10f, 5f)

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#304567"))
        colors.add(Color.parseColor("#309967"))
        colors.add(Color.parseColor("#476567"))
        colors.add(Color.parseColor("#890567"))
        colors.add(Color.parseColor("#a35567"))
        colors.add(Color.parseColor("#ff5f67"))
        colors.add(Color.parseColor("#3ca567"))

        for (i in 0 until numberAmount.size) {
            pieEntries.add(PieEntry(numberAmount[i], typeAmountMap[i]))
        }
        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.colors = colors
        pieDataSet.sliceSpace = sliceSpace
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        
        binding.budgetPieChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            holeRadius = this@BudgetFragment.holeRadius
            data = pieData
            centerText = getString(R.string.budget_fragment)
            setDrawEntryLabels(false)
            invalidate()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BudgetFragment()
    }
}