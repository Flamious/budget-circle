package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
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

        budgetData.totalSum.observe(this, {
            binding.sumText.text = "%.2f".format(it)
        })
        budgetData.updateSum()
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    when (result.data?.getStringExtra("type").toString()) {
                        "newAccount" -> {
                            val sum: Float = result.data?.getFloatExtra("newAccountBudget", 0f)!!
                            val name: String = result.data?.getStringExtra("newAccountName")!!
                            /*budgetData.addEarning(sum)*/
                            budgetData.addToBudgetTypesList(
                                BudgetType(
                                    name,
                                    sum,
                                    true
                                )
                            )
                            if (sum > 0f) {
                                budgetData.addToOperationList(
                                    HistoryItem(
                                        1,
                                        sum,
                                        "New account: $name",
                                        "Other",
                                        Date(),
                                        resources.getColor(R.color.blue_button),
                                        false
                                    )
                                )
                            }

                            Toast.makeText(
                                activity,
                                "Added",
                                Toast.LENGTH_SHORT
                            ).show()
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
        setChart()
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

    private fun setChart() {
        val values = arrayListOf(12f, 20f, 45f, 62f, 15f)
        var i = 0f
        for (n in values) {
            i += n
        }
        val titles = resources.getStringArray(R.array.budget_titles).toCollection(ArrayList())
        val colors = resources.getIntArray(R.array.budget_colors).toCollection(ArrayList())
        if (i > 0)
            PieChartSetter.setChart(titles, values, colors, binding.budgetPieChart)
        else
            PieChartSetter.setChart(
                arrayListOf("No entries"), arrayListOf(100f),
                arrayListOf(resources.getColor(R.color.no_money_op)), binding.budgetPieChart
            )
    }

    private fun addAccount() {
        val intent = Intent(activity, BudgetFormActivity::class.java)
        launcher?.launch(intent)
    }

    private fun addExchange() {
        val intent = Intent(activity, BudgetExchangeActivity::class.java)
        intent.putExtra("types", budgetData.budgetTypes.value?.toTypedArray())
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