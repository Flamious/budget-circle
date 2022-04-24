package com.example.budgetcircle.forms

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityBudgetFormBinding
import com.example.budgetcircle.fragments.UserFragment
import com.example.budgetcircle.settings.SumInputFilter
import com.example.budgetcircle.viewmodel.BudgetDataApi

class BudgetFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetFormBinding
    private var isEdit: Boolean = false

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.appear_short_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetFormBinding.inflate(layoutInflater)
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
        val textColor: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (BudgetDataApi.mode.value!! == UserFragment.NIGHT) {
                textColor = ContextCompat.getColor(
                    this@BudgetFormActivity,
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@BudgetFormActivity,
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@BudgetFormActivity,
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@BudgetFormActivity,
                    R.color.darker_grey
                )


                accountFormActivityHeaderLayout.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                accountFormActivityAddButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                accountFormActivityBackButton.backgroundTintList =
                    ColorStateList.valueOf(mainColor)
                accountFormActivityLayout.setBackgroundColor(backgroundColor)

                setFieldColor(
                    binding.accountFormActivityBudgetSum,
                    mainColor,
                    textColor,
                    textSecondary
                )
                setFieldColor(
                    binding.accountFormActivityName,
                    mainColor,
                    textColor,
                    textSecondary
                )
            }
        }
    }

    private fun setFieldColor(
        editText: TextView,
        mainColor: Int,
        textColor: Int,
        textSecondary: Int
    ) {
        editText.backgroundTintList = ColorStateList.valueOf(mainColor)
        editText.highlightColor = mainColor
        editText.setLinkTextColor(mainColor)
        editText.setTextColor(textColor)
        editText.setHintTextColor(textSecondary)
    }

    private fun setButtons() {
        binding.accountFormActivityAddButton.setOnClickListener {
            add()
        }
        binding.accountFormActivityBackButton.setOnClickListener {
            exit()
        }
    }

    private fun setInitialValues() {
        isEdit = intent.getStringExtra("edit") != null
        binding.accountFormActivityBudgetSum.filters = arrayOf<InputFilter>(SumInputFilter())
        if (isEdit) {
            binding.apply {
                accountFormActivityName.setText(intent.getStringExtra("accountName")!!)
                accountFormActivityBudgetSum.setText(
                    intent.getDoubleExtra(
                        "newAccountBudget",
                        0.0
                    ).toString()
                )
                accountFormActivityAddButton.text = resources.getText(R.string.edit_account)
                accountFormActivityTitle.text = resources.getText(R.string.edit_account)
            }
        }
    }

    //endregion
    //region Methods
    private fun appear() {
        binding.accountFormActivityHeaderLayout.startAnimation(appear)
        binding.accountFormActivityScrollView.startAnimation(appear)
        binding.accountFormActivityAddButton.startAnimation(appear)
    }

    private fun checkFields(): Boolean {
        val sum = binding.accountFormActivityBudgetSum.text.toString().toDoubleOrNull()
        var isValid = true
        binding.accountFormActivityBudgetSum.apply {
            error = null
            if (sum == null) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }
        binding.accountFormActivityName.apply {
            error = null
            if (text.isNullOrBlank()) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }

        return isValid
    }

    private fun add() {
        if (checkFields()) {
            val intent = Intent()
            intent.putExtra("type", if (isEdit) "editAccount" else "newAccount")
            intent.putExtra("newAccountName", binding.accountFormActivityName.text.toString())
            intent.putExtra(
                "newAccountBudget",
                binding.accountFormActivityBudgetSum.text.toString().toDouble()
            )
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