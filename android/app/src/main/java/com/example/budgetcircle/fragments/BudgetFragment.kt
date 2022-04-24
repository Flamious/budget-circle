package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
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
import com.example.budgetcircle.settings.BarChartSetter
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.PieChartSetter
import com.example.budgetcircle.viewmodel.BudgetDataApi
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList


class BudgetFragment : Fragment() {
    lateinit var binding: FragmentBudgetBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val budgetDataApi: BudgetDataApi by activityViewModels()
    private var isPieChart = true

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
    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.context,
            R.anim.appear_short_anim
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
        setTheme()
        setObservation()
        setLauncher()
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeOrientation()
    }

    override fun onStart() {
        super.onStart()
        appear()
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

    private fun setTheme() {
        if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
            binding.apply {
                val textColor = ContextCompat.getColor(
                    this@BudgetFragment.requireContext(),
                    R.color.light_grey
                )
                val textSecondary = ContextCompat.getColor(
                    this@BudgetFragment.requireContext(),
                    R.color.grey
                )
                val backgroundColor = ContextCompat.getColor(
                    this@BudgetFragment.requireContext(),
                    R.color.dark_grey
                )
                val mainColor = ContextCompat.getColor(
                    this@BudgetFragment.requireContext(),
                    R.color.darker_grey
                )

                budgetFragmentChangeChartButton.backgroundTintList = ColorStateList.valueOf(mainColor)
                budgetFragmentListButton.backgroundTintList = ColorStateList.valueOf(mainColor)
                budgetFragmentExchangeButton.backgroundTintList = ColorStateList.valueOf(mainColor)
                budgetFragmentTypeListButton.backgroundTintList = ColorStateList.valueOf(mainColor)
                budgetFragmentAddAccountButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                budgetFragmentHeaderLayout.setBackgroundColor(mainColor)
                budgetFragmentLayout.backgroundTintList = ColorStateList.valueOf(backgroundColor)

                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    budgetFragmentKindText.setTextColor(textColor)
                    budgetFragmentSumTitleTextView?.setTextColor(textSecondary)
                    budgetFragmentAccountTitleTextView?.setTextColor(textSecondary)
                } else {
                    budgetFragmentKindText.setTextColor(textSecondary)
                }

                budgetFragmentSumText.setTextColor(textColor)
            }
        }
    }

    private fun setButtons() {
        binding.budgetFragmentAddAccountButton.setOnClickListener {
            addAccount()
            showHiddenButtons()
        }
        binding.budgetFragmentListButton.setOnClickListener {
            showHiddenButtons()
        }
        binding.budgetFragmentExchangeButton.setOnClickListener {
            addExchange()
            if (isClicked) showHiddenButtons()
        }
        binding.budgetFragmentTypeListButton.setOnClickListener {
            openBudgetTypeList()
        }
        binding.budgetFragmentChangeChartButton.setOnClickListener {
            changeChart()
        }
    }

    private fun setBarChart(budgetTypes: List<BudgetType>) {
        val values = Array(budgetTypes.size) { index -> budgetTypes[index].sum }
        val titles = Array(budgetTypes.size) { index -> budgetTypes[index].title }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors = if (BudgetDataApi.mode.value!! == UserFragment.DAY)
            resources.getIntArray(R.array.budget_colors).toCollection(ArrayList())
        else
            resources.getIntArray(R.array.dark_colors).toCollection(ArrayList())
        if (sum > 0)
            BarChartSetter.setChart(
                titles,
                values,
                colors,
                binding.budgetFragmentBarChart,
                ContextCompat.getColor(
                    this.requireContext(),
                    if (BudgetDataApi.mode.value!! == UserFragment.DAY) R.color.text_primary else R.color.light_grey
                )
            )
        else
            BarChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(0.0),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                binding.budgetFragmentBarChart,
                ContextCompat.getColor(
                    this.requireContext(),
                    if (BudgetDataApi.mode.value!! == UserFragment.DAY) R.color.text_primary else R.color.light_grey
                ),
                true
            )
    }

    private fun setPieChart(budgetTypes: List<BudgetType>) {
        val values = Array(budgetTypes.size) { index -> budgetTypes[index].sum }
        val titles = Array(budgetTypes.size) { index -> budgetTypes[index].title }
        var sum = 0.0
        for (n in values) {
            sum += n
        }
        sum = DoubleFormatter.format(sum)
        val colors = if (BudgetDataApi.mode.value!! == UserFragment.DAY)
            resources.getIntArray(R.array.budget_colors).toCollection(ArrayList())
        else
            resources.getIntArray(R.array.dark_colors).toCollection(ArrayList())

        val holeColor = ContextCompat.getColor(
            this.requireContext(),
            if (BudgetDataApi.mode.value!! == UserFragment.DAY) R.color.light_grey else R.color.dark_grey
        )
        if (sum > 0)
            PieChartSetter.setChart(
                titles,
                values,
                colors,
                sum,
                resources.getString(R.string.total),
                binding.budgetFragmentPieChart,
                binding.budgetFragmentSumText,
                binding.budgetFragmentKindText,
                holeColor,
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
        else
            PieChartSetter.setChart(
                arrayOf(resources.getString(R.string.no_entries)),
                arrayOf(100.0),
                arrayListOf(ContextCompat.getColor(this.requireContext(), R.color.no_money_op)),
                sum,
                resources.getString(R.string.no_entries),
                binding.budgetFragmentPieChart,
                binding.budgetFragmentSumText,
                binding.budgetFragmentKindText,
                holeColor,
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
            if (it != null) {
                if (isPieChart)
                    setPieChart(it)
                else
                    setBarChart(it)
                binding.budgetFragmentExchangeButton.visibility =
                    if (it.count() > 1) View.VISIBLE else View.GONE
            }
        })
    }

    //endregion
    //region Methods
    private fun changeChart() {
        binding.apply {
            if (isPieChart) {
                budgetFragmentPieChart.visibility = View.INVISIBLE
                budgetFragmentBarChart.visibility = View.VISIBLE
                budgetFragmentInfoLayout.visibility = View.INVISIBLE
                budgetFragmentChangeChartButton.setImageResource(R.drawable.ic_pie_chart)

                setBarChart(budgetDataApi.budgetTypes.value!!)
            } else {
                budgetFragmentBarChart.visibility = View.INVISIBLE
                budgetFragmentPieChart.visibility = View.VISIBLE
                budgetFragmentInfoLayout.visibility = View.VISIBLE
                budgetFragmentChangeChartButton.setImageResource(R.drawable.ic_bar_chart)

                setPieChart(budgetDataApi.budgetTypes.value!!)
            }
            isPieChart = !isPieChart
        }
    }

    private fun appear() {
        binding.apply {
            budgetFragmentHeaderLayout.startAnimation(appear)
            budgetFragmentSumText.startAnimation(appear)
            budgetFragmentKindText.startAnimation(appear)
            budgetFragmentExchangeButton.startAnimation(appear)
            budgetFragmentListButton.startAnimation(appear)
            budgetFragmentSumTitleTextView?.startAnimation(appear)
            budgetFragmentAccountTitleTextView?.startAnimation(appear)
        }
    }

    private fun showHiddenButtons() {
        binding.apply {
            setAnimation(
                isClicked,
                budgetFragmentListButton,
                budgetFragmentHiddenButtonsLayout,
                budgetFragmentAddAccountButton,
                budgetFragmentTypeListButton
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