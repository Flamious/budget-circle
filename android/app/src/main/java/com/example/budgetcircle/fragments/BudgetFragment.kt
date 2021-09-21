package com.example.budgetcircle.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
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
import androidx.fragment.app.activityViewModels
import com.example.budgetcircle.viewmodel.BudgetData
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.FragmentBudgetBinding
import com.example.budgetcircle.forms.BudgetExchangeActivity
import com.example.budgetcircle.forms.BudgetFormActivity
import com.example.budgetcircle.forms.EarningsFormActivity
import com.example.budgetcircle.settings.PieChartSetter
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BudgetFragment : Fragment() {
    lateinit var binding: FragmentBudgetBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

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

    val budgetData: BudgetData by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Toast.makeText(
                        activity,
                        result.data?.getStringExtra("Ha").toString(),
                        Toast.LENGTH_LONG
                    ).show()
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
        binding.addAccountButton.setOnClickListener() {
            addAccount()
        }
        binding.listButton.setOnClickListener() {
            binding.apply {

                setAnimation(
                    isClicked,
                    listButton,
                    addAccountButton,
                    repetitiveOpListButton,
                    stocksListButton
                )
            }
            isClicked = !isClicked
        }
        binding.exchangeButton.setOnClickListener() {
            addExchange()
        }
    }

    private fun setChart() {
        val values = arrayListOf(12f, 20f, 45f, 62f, 15f)
        /*val values = arrayListOf(0f, 0f, 0f)*/
        var i: Float = 0f
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
        launcher?.launch(intent)
    }

    private fun setAnimation(
        isClicked: Boolean,
        listButton: FloatingActionButton,
        vararg buttons: FloatingActionButton
    ) {
        for (button in buttons) {
            button.startAnimation(if (isClicked) toBottom else fromBottom)
            /*button.visibility = if (isClicked) View.GONE else View.VISIBLE*/
            button.visibility = View.VISIBLE
            button.isClickable = !isClicked
        }
        listButton.startAnimation(if (isClicked) rotateClose else rotateOpen)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BudgetFragment()
    }
}