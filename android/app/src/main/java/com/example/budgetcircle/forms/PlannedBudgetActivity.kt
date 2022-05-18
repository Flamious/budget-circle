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
import com.example.budgetcircle.databinding.ActivityBudgetFormBinding
import com.example.budgetcircle.databinding.ActivityPlannedBudgetBinding
import com.example.budgetcircle.settings.DoubleFormatter
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.settings.SumInputFilter

class PlannedBudgetActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlannedBudgetBinding
    private var isEdit: Boolean = false

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.appear_short_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlannedBudgetBinding.inflate(layoutInflater)
        setInitialValues()
        setButtons()
        setTheme()
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
        val textPrimary: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (Settings.isNight()) {
                textPrimary = ContextCompat.getColor(
                    this@PlannedBudgetActivity,
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@PlannedBudgetActivity,
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@PlannedBudgetActivity,
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@PlannedBudgetActivity,
                    R.color.darker_grey
                )


                plannedBudgetFormActivityHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                plannedBudgetFormActivityAddButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                plannedBudgetFormActivityBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                plannedBudgetFormLayout.setBackgroundColor(backgroundColor)

                Settings.setFieldColor(
                    mainColor,
                    textPrimary,
                    textSecondary,
                    binding.plannedBudgetFormActivityPlannedEarnings,
                    binding.plannedBudgetFormActivityPlannedExpenses
                )
            }
        }
    }

    private fun setButtons() {
        binding.plannedBudgetFormActivityAddButton.setOnClickListener {
            accept()
        }
        binding.plannedBudgetFormActivityBackButton.setOnClickListener {
            exit()
        }
    }

    private fun setInitialValues() {
        isEdit = intent.getStringExtra("edit") != null
        binding.plannedBudgetFormActivityPlannedEarnings.filters =
            arrayOf<InputFilter>(SumInputFilter(15))
        binding.plannedBudgetFormActivityPlannedExpenses.filters =
            arrayOf<InputFilter>(SumInputFilter(15))

        if (isEdit) {
            binding.apply {
                plannedBudgetFormActivityPlannedEarnings.setText(
                    intent.getDoubleExtra(
                        "plannedEarnings",
                        0.0
                    ).toString()
                )
                plannedBudgetFormActivityPlannedExpenses.setText(
                    intent.getDoubleExtra(
                        "plannedExpenses",
                        0.0
                    ).toString()
                )
            }
        }

        val title = intent.getStringExtra("date")!!
        binding.plannedBudgetFormActivityTitle.text = title
    }
    //endregion

    //region Methods
    private fun appear() {
        binding.plannedBudgetFormActivityHeaderLayout.startAnimation(appear)
        binding.plannedBudgetFormActivityScrollView.startAnimation(appear)
        binding.plannedBudgetFormActivityAddButton.startAnimation(appear)
    }

    private fun checkFields(): Boolean {
        val earnings = binding.plannedBudgetFormActivityPlannedEarnings.text.toString().toDoubleOrNull()
        val expenses = binding.plannedBudgetFormActivityPlannedExpenses.text.toString().toDoubleOrNull()
        var isValid = true

        binding.plannedBudgetFormActivityPlannedEarnings.apply {
            error = null
            if (earnings == null) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        binding.plannedBudgetFormActivityPlannedExpenses.apply {
            error = null
            if (expenses == null) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun accept() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("type", if (isEdit) "edit" else "new")
            intent.putExtra("plannedEarnings", DoubleFormatter.format(binding.plannedBudgetFormActivityPlannedEarnings.text.toString().toDouble()))
            intent.putExtra("plannedExpenses", DoubleFormatter.format(binding.plannedBudgetFormActivityPlannedExpenses.text.toString().toDouble()))
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