package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.service.autofill.Dataset
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.databinding.FragmentBudgetBinding
import com.example.budgetcircle.forms.BudgetExchangeActivity
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.lists.BudgetTypeListFragment
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetData
/*import com.example.budgetcircle.viewmodel.items.BudgetType*/
import com.example.budgetcircle.viewmodel.items.HistoryItem
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList


class BudgetFragment : Fragment() {
    lateinit var binding: FragmentBudgetBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()

    /* Animations */
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.to_bottom_anim
        )
    }
    private var isClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        budgetData.budgetTypes.observe(this, {
            setChart(it)
        })

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    when (result.data?.getStringExtra("type").toString()) {
                        "newAccount" -> {
                            val sum: Double = result.data?.getDoubleExtra("newAccountBudget", 0.0)!!
                            val name: String = result.data?.getStringExtra("newAccountName")!!
                            budgetData.addToBudgetTypesList(
                                BudgetType(
                                    name,
                                    sum,
                                    true
                                )
                            )

                            Toast.makeText(
                                activity,
                                "Added",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        "exchange" -> {
                            val sum: Double = result.data?.getDoubleExtra("sum", 0.0)!!
                            val from: Int = result.data?.getIntExtra("fromIndex", 0)!!
                            val to: Int = result.data?.getIntExtra("toIndex", 0)!!

                            budgetData.addExpense(
                                sum,
                                budgetData.expenseTypes[budgetData.expenseTypes.lastIndex].id, //Other
                                budgetData.budgetTypes.value!![from].id
                            )

                            budgetData.addEarning(
                                sum,
                                budgetData.expenseTypes[budgetData.expenseTypes.lastIndex].id, //Other
                                budgetData.budgetTypes.value!![to].id
                            )
                        }
                        else -> {
                            Toast.makeText(
                                activity,
                                "Toast",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetBinding.inflate(inflater)
        setButtons()
        return binding.root
    }

    private fun setButtons() {
        binding.addAccountButton.setOnClickListener {
            addAccount()
            showHiddenButtons()
        }
        binding.listButton.setOnClickListener {
            showHiddenButtons()
        }
        binding.exchangeButton.setOnClickListener {
            addExchange()
        }
        binding.typeListButton.setOnClickListener {
            openBudgetTypeList()
        }
    }

    private fun showHiddenButtons() {
        binding.apply {
            setAnimation(
                isClicked,
                listButton,
                hiddenButtonsLayout,
                addAccountButton,
                typeListButton,
                repetitiveOpListButton,
                stocksListButton
            )
        }
        isClicked = !isClicked
    }

    private fun setChart(budgetTypes: List<BudgetType>) {
        val values = Array(budgetTypes.size) { index -> budgetTypes[index].sum }
        val titles = Array(budgetTypes.size) { index -> budgetTypes[index].title }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        val colors = resources.getIntArray(R.array.budget_colors).toCollection(ArrayList())
        if (sum > 0)
            PieChartSetter.setChart(
                titles,
                values,
                colors,
                sum,
                resources.getString(R.string.total),
                binding.budgetPieChart,
                binding.sumText,
                binding.kindText
            )
        else
            PieChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(100.0),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                sum,
                resources.getString(R.string.no_entries),
                binding.budgetPieChart,
                binding.sumText,
                binding.kindText,
                true
            )
    }


    private fun addAccount() {
        val intent = Intent(activity, BudgetFormActivity::class.java)
        launcher?.launch(intent)
    }

    private fun addExchange() {
        val intent = Intent(activity, BudgetExchangeActivity::class.java)
        intent.putExtra(
            "budgetTypes",
            Array(budgetData.budgetTypes.value!!.size) { index -> budgetData.budgetTypes.value!![index].title })
        launcher?.launch(intent)
    }

    private fun openBudgetTypeList() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, BudgetTypeListFragment())
            ?.commit()
    }

    private fun setAnimation(
        isClicked: Boolean,
        listButton: FloatingActionButton,
        hiddenButtonsLayout: ConstraintLayout,
        vararg buttons: FloatingActionButton
    ) {
        hiddenButtonsLayout.startAnimation(if (isClicked) toBottom else fromBottom)
        hiddenButtonsLayout.visibility = if (isClicked) View.GONE else View.VISIBLE
        for (button in buttons) {
            button.isClickable = !isClicked
        }
        listButton.startAnimation(if (isClicked) rotateClose else rotateOpen)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BudgetFragment()
    }
}