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
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentEarningsBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.forms.EarningsFormActivity
import com.example.budgetcircle.lists.BudgetTypeListFragment
import com.example.budgetcircle.lists.OperationTypeListFragment
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.models.Operation
import com.example.budgetcircle.viewmodel.models.OperationSum
import kotlin.collections.ArrayList

class EarningsFragment : Fragment() {
    lateinit var binding: FragmentEarningsBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetDataApi: BudgetDataApi by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEarningsBinding.inflate(inflater)
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
        binding.listButton.setOnClickListener {
            openTypeList()
        }
        binding.addEarningButton.setOnClickListener {
            addEarning()
        }
        binding.periodText.setOnClickListener {
            Dialogs().chooseOne(
                this.requireContext(),
                resources.getString(R.string.choosingPeriod),
                resources.getStringArray(R.array.periodsString),
                resources.getIntArray(R.array.periodsInt).toTypedArray(),
                budgetDataApi.earningsDateString,
                budgetDataApi.earningsDate
            )
        }
    }

    private fun setChart(earningSums: List<OperationSum>) {
        val values = Array(earningSums.size) { index -> earningSums[index].sum }
        val titles = Array(earningSums.size) { index -> earningSums[index].type }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors = resources.getIntArray(R.array.earning_colors).toCollection(ArrayList())
        if (sum > 0)
            PieChartSetter.setChart(
                titles,
                values,
                colors,
                sum,
                resources.getString(R.string.total),
                binding.earningsPieChart,
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
                binding.earningsPieChart,
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
                    val earningTypeIndex = result.data?.getIntExtra("typeIndex", 0)!!
                    val earningTitle = result.data?.getStringExtra("title")!!
                    val earningCommentary = result.data?.getStringExtra("commentary")!!
                    budgetDataApi.addOperation(Operation(-1,
                        earningTitle,
                        result.data?.getDoubleExtra("sum", 0.0)!!,
                        "",
                        budgetDataApi.earningTypes.value!![earningTypeIndex].id,
                        budgetDataApi.budgetTypes.value!![budgetTypeIndex].id,
                        earningCommentary,
                        false
                    ))

                    Toast.makeText(
                        activity,
                        resources.getString(R.string.added),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setObservation() {
        budgetDataApi.earningsDateString.observe(this.viewLifecycleOwner, {
            binding.periodText.text = it
        })
        budgetDataApi.earningsDate.observe(this.viewLifecycleOwner, {
            budgetDataApi.getEarningSums(it)
        })
        budgetDataApi.earningSums.observe(this.viewLifecycleOwner, {
            if(it != null) {
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
            ?.replace(R.id.fragmentPanel, EarningsFragment())
            ?.commit()
    }

    private fun addEarning() {
        val intent = Intent(activity, EarningsFormActivity::class.java)
        intent.putExtra(
            "budgetTypes",
            Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].title })
        intent.putExtra(
            "earningTypes",
            Array(budgetDataApi.earningTypes.value!!.size) { index -> budgetDataApi.earningTypes.value!![index].title })
        launcher?.launch(intent)
    }

    private fun openTypeList() {
        budgetDataApi.isExpense = false
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, OperationTypeListFragment())
            ?.commit()
    }
    //endregion
}