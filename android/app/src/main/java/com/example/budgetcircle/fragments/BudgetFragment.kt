package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentBudgetBinding
import com.example.budgetcircle.forms.BudgetExchangeActivity
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.lists.BudgetTypeListFragment
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList


class BudgetFragment : Fragment() {
    lateinit var binding: FragmentBudgetBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetData: BudgetData by activityViewModels()
    private val budgetDataApi: BudgetDataApi by activityViewModels()

    //region Animations
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
    private val fromLeft: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.from_left_anim
        )
    }
    private val toLeft: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.to_left_anim
        )
    }
    private var isClicked = false
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBudgetBinding.inflate(inflater)
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
    private fun setAnimation(
        isClicked: Boolean,
        listButton: FloatingActionButton,
        hiddenButtonsLayout: ConstraintLayout,
        vararg buttons: FloatingActionButton
    ) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            hiddenButtonsLayout.startAnimation(if (isClicked) toBottom else fromBottom)
        else
            hiddenButtonsLayout.startAnimation(if (isClicked) toLeft else fromLeft)
        hiddenButtonsLayout.visibility = if (isClicked) View.GONE else View.VISIBLE
        for (button in buttons) {
            button.isClickable = !isClicked
        }
        listButton.startAnimation(if (isClicked) rotateClose else rotateOpen)
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

    private fun setChart(budgetTypes: List<BudgetType>) {
        val values = Array(budgetTypes.size) { index -> budgetTypes[index].sum }
        val titles = Array(budgetTypes.size) { index -> budgetTypes[index].title }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
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
                binding.budgetPieChart,
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
                    when (result.data?.getStringExtra("type").toString()) {
                        "newAccount" -> {
                            val sum: Double = result.data?.getDoubleExtra("newAccountBudget", 0.0)!!
                            val name: String = result.data?.getStringExtra("newAccountName")!!
                            budgetDataApi.addBudgetType(
                                BudgetType(
                                    -1,
                                    name,
                                    sum,
                                    true
                                )
                            )
                        }
                        "exchange" -> {
                            val sum: Double = result.data?.getDoubleExtra("sum", 0.0)!!
                            val from: Int = result.data?.getIntExtra("fromIndex", 0)!!
                            val to: Int = result.data?.getIntExtra("toIndex", 0)!!

                            budgetDataApi.makeExchange(
                                budgetDataApi.budgetTypes.value!![from],
                                budgetDataApi.budgetTypes.value!![to],
                                sum
                            )
                        }
                        else -> {
                            Toast.makeText(
                                activity,
                                resources.getString(R.string.done),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

    }

    private fun setObservation() {
        budgetDataApi.budgetTypes.observe(this.viewLifecycleOwner, {
            if(it != null) {
                setChart(it)
            }
        })
    }
    //endregion
    //region Methods
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

    private fun addAccount() {
        val intent = Intent(activity, BudgetFormActivity::class.java)
        launcher?.launch(intent)
    }

    private fun addExchange() {
        val intent = Intent(activity, BudgetExchangeActivity::class.java)
        intent.putExtra(
            "budgetTypes",
            Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].title })
        intent.putExtra(
            "budgetTypesSums",
            Array(budgetDataApi.budgetTypes.value!!.size) { index -> budgetDataApi.budgetTypes.value!![index].sum })
        launcher?.launch(intent)
    }

    private fun openBudgetTypeList() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, BudgetTypeListFragment())
            ?.commit()
    }

    private fun changeOrientation() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentPanel, BudgetFragment())
            ?.commit()
    }
    //endregion
}