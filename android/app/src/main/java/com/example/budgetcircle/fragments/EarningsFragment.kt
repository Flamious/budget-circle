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
import com.example.budgetcircle.R
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.databinding.FragmentEarningsBinding
import com.example.budgetcircle.forms.EarningsFormActivity
import com.example.budgetcircle.settings.PieChartSetter

class EarningsFragment : Fragment() {
    lateinit var binding: FragmentEarningsBinding
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
        binding = FragmentEarningsBinding.inflate(inflater)
        setChart()
        setButtons()
        return binding.root
    }

    private fun setButtons() {
        binding.addButton.setOnClickListener() {
            addEarning()
        }
    }

    private fun setChart() {
        val values = arrayListOf(12f, 20f, 15f, 62f, 15f, 92f, 11f, 3f)
        /*val values = arrayListOf(0f, 0f, 0f)*/
        var i: Float = 0f
        for(n in values) {
            i += n
        }
        val titles = resources.getStringArray(R.array.earning_titles).toCollection(ArrayList())
        val colors = resources.getIntArray(R.array.earning_colors).toCollection(ArrayList())
        if (i > 0)
            PieChartSetter.setChart(titles, values, colors, binding.earningsPieChart)
        else
            PieChartSetter.setChart(arrayListOf("No entries"), arrayListOf(100f),
                arrayListOf(resources.getColor(R.color.no_money_op)), binding.earningsPieChart)
    }

    private fun addEarning() {
        val intent = Intent(activity, EarningsFormActivity::class.java)
        launcher?.launch(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EarningsFragment()
    }
}