package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentBudgetBinding
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.forms.EarningsFormActivity
import com.example.budgetcircle.settings.PieChartSetter
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet




class BudgetFragment : Fragment() {
    lateinit var binding: FragmentBudgetBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    val budgetData: BudgetData by activityViewModels()


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
        binding = FragmentBudgetBinding.inflate(inflater)
        setChart()
        setButtons()
        return binding.root
    }

    private fun setButtons() {
        binding.addButton.setOnClickListener() {
            addAccount()
        }
    }

    private fun setChart() {
        val values = arrayListOf(12f, 20f, 45f, 62f, 15f)
        /*val values = arrayListOf(0f, 0f, 0f)*/
        var i: Float = 0f
        for(n in values) {
            i += n
        }
        val titles = resources.getStringArray(R.array.budget_titles).toCollection(ArrayList())
        val colors = resources.getIntArray(R.array.budget_colors).toCollection(ArrayList())
        if (i > 0)
            PieChartSetter.setChart(titles, values, colors, binding.budgetPieChart)
        else
            PieChartSetter.setChart(arrayListOf("No entries"), arrayListOf(100f),
                arrayListOf(resources.getColor(R.color.no_money_op)), binding.budgetPieChart)
    }

    private fun addAccount() {
        val intent = Intent(activity, BudgetFormActivity::class.java)
        launcher?.launch(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BudgetFragment()
    }
}