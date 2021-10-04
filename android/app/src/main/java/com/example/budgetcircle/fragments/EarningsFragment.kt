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
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentEarningsBinding
import com.example.budgetcircle.forms.EarningsFormActivity
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EarningsFragment : Fragment() {
    lateinit var binding: FragmentEarningsBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        budgetData.earningsSum.observe(this, {
            binding.sumText.text = "%.2f".format(it)
        })

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    budgetData.addEarning(result.data?.getFloatExtra("sum", 0f))

                    budgetData.addToOperationList(
                        HistoryItem(
                            1,
                            result.data?.getFloatExtra("sum", 0f)!!,
                            result.data?.getStringExtra("title")!!,
                            result.data?.getStringExtra("type")!!,
                            SimpleDateFormat("dd.MM.yyyy").parse(result.data?.getStringExtra("date")!!),
                            resources.getColor(R.color.blue_button),
                            result.data?.getBooleanExtra("isRep", false)!!))

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
        binding = FragmentEarningsBinding.inflate(inflater)
        setChart()
        setButtons()
        return binding.root
    }

    private fun setButtons() {
        binding.addEarningButton.setOnClickListener {
            addEarning()
        }
    }

    private fun setChart() {
        val values = arrayListOf(12f, 20f, 15f, 62f, 15f, 92f, 11f, 3f)
        /*val values = arrayListOf(0f, 0f, 0f)*/
        var i = 0f
        for (n in values) {
            i += n
        }
        val titles = resources.getStringArray(R.array.earning_titles).toCollection(ArrayList())
        val colors = resources.getIntArray(R.array.earning_colors).toCollection(ArrayList())
        if (i > 0)
            PieChartSetter.setChart(titles, values, colors, binding.earningsPieChart)
        else
            PieChartSetter.setChart(
                arrayListOf("No entries"), arrayListOf(100f),
                arrayListOf(resources.getColor(R.color.no_money_op)), binding.earningsPieChart
            )
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