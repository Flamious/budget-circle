package com.example.budgetcircle.forms

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityBudgetExchangeBinding
import com.example.budgetcircle.dialogs.Dialogs
import com.example.budgetcircle.dialogs.Index
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.settings.SumInputFilter

class BudgetExchangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetExchangeBinding
    private var chosenBudgetTypeFrom: Index = Index(0)
    private var chosenBudgetTypeTo: Index = Index(1)
    lateinit var budgetTypes: Array<String>
    private lateinit var budgetTypesSums: Array<Double>
    private var isEdit: Boolean = false

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.appear_short_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetExchangeBinding.inflate(layoutInflater)
        setInitialValues()
        setTheme()
        setButtons()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        appear()
    }

    override fun onBackPressed() {
        exit()
    }

    //region Setting
    private fun setTheme() {
        binding.apply {
            if (Settings.isNight()) {
                val textPrimary = ContextCompat.getColor(
                    this@BudgetExchangeActivity,
                    R.color.light_grey
                )
                val textSecondary = ContextCompat.getColor(
                    this@BudgetExchangeActivity,
                    R.color.grey
                )
                val backgroundColor = ContextCompat.getColor(
                    this@BudgetExchangeActivity,
                    R.color.dark_grey
                )
                val mainColor = ContextCompat.getColor(
                    this@BudgetExchangeActivity,
                    R.color.darker_grey
                )

                budgetExchangeActivityListHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                budgetExchangeActivityAddButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                budgetExchangeActivityBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                budgetExchangeActivityLayout.setBackgroundColor(backgroundColor)
                budgetExchangeActivityListFrom.setTextColor(textPrimary)
                budgetExchangeActivityListTo.setTextColor(textPrimary)
                budgetExchangeActivityTitleFrom.setTextColor(textSecondary)
                budgetExchangeActivityTitleTo.setTextColor(textSecondary)

                Settings.setFieldColor(mainColor, textPrimary, textSecondary, budgetExchangeActivityBudgetSum)
            }
        }
    }

    private fun setButtons() {
        val effectColor: Int = if (Settings.mode == Settings.DAY) {
            R.style.greenEdgeEffect
        } else {
            R.style.darkEdgeEffect
        }

        binding.budgetExchangeActivityAddButton.setOnClickListener {
            accept()
        }
        binding.budgetExchangeActivityBackButton.setOnClickListener {
            exit()
        }
        binding.budgetExchangeActivityListFrom.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.budgetExchangeActivityListFrom,
                chosenBudgetTypeFrom,
                effectColor
            )
        }
        binding.budgetExchangeActivityListTo.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.account),
                budgetTypes,
                binding.budgetExchangeActivityListTo,
                chosenBudgetTypeTo,
                effectColor
            )
        }
    }

    private fun setEditPage() {
        binding.budgetExchangeActivityBudgetSum.setText(intent.getDoubleExtra("exchangeSum", 0.0).toString())
        chosenBudgetTypeFrom.value = intent.extras?.getInt("fromIndex")!!
        chosenBudgetTypeTo.value = intent.extras?.getInt("toIndex")!!
        binding.budgetExchangeActivityAddButton.text = resources.getText(R.string.edit)
        binding.budgetExchangeActivityListFrom.text = budgetTypes[chosenBudgetTypeFrom.value]
        binding.budgetExchangeActivityListTo.text = budgetTypes[chosenBudgetTypeTo.value]
        isEdit = true
    }

    private fun setInitialValues() {
        budgetTypes = intent.extras?.getStringArray("budgetTypes")!!
        if (intent.extras?.getBoolean("isEdit", false) == false) {
            binding.budgetExchangeActivityListFrom.text = budgetTypes[0]
            binding.budgetExchangeActivityListTo.text = budgetTypes[1]
            budgetTypesSums =
                (intent.extras?.getSerializable("budgetTypesSums")!! as Array<*>).filterIsInstance<Double>()
                    .toTypedArray()
        } else {
            setEditPage()
            budgetTypesSums = arrayOf()
        }
        binding.budgetExchangeActivityBudgetSum.filters = arrayOf<InputFilter>(SumInputFilter())
    }

    //endregion
    //region Methods
    private fun appear() {
        binding.budgetExchangeActivityListHeaderLayout.startAnimation(appear)
        binding.budgetExchangeActivityAddButton.startAnimation(appear)
        binding.budgetExchangeActivityScrollView.startAnimation(appear)
    }

    private fun checkFields(): Boolean {
        val sum = binding.budgetExchangeActivityBudgetSum.text.toString().toDoubleOrNull()
        var isValid = true
        binding.budgetExchangeActivityBudgetSum.apply {
            error = null
            when {
                sum == null -> {
                    error = resources.getString(R.string.empty_field)
                    isValid = false
                }
                sum <= 0.0 -> {
                    error = resources.getString(R.string.zero_sum)
                    isValid = false
                }
            }
        }

        return isValid
    }

    private fun accept() {
        if (checkFields()) {
            val intent = Intent()
            if(isEdit) {
                intent.putExtra("budgetTypeIndex", chosenBudgetTypeFrom.value)
                intent.putExtra("typeIndex", chosenBudgetTypeTo.value)
            }
            else {
                intent.putExtra("fromIndex", chosenBudgetTypeFrom.value)
                intent.putExtra("toIndex", chosenBudgetTypeTo.value)
            }
            intent.putExtra("type", "exchange")
            intent.putExtra("sum", binding.budgetExchangeActivityBudgetSum.text.toString().toDouble())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
    //endregion
}