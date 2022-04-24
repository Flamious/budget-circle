package com.example.budgetcircle.forms

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityOperationTypeFormBinding
import com.example.budgetcircle.fragments.UserFragment
import com.example.budgetcircle.viewmodel.BudgetDataApi


class OperationTypeFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityOperationTypeFormBinding
    private var isEdit: Boolean = false
    private var isExpense = false

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.appear_short_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperationTypeFormBinding.inflate(layoutInflater)
        setTheme()
        setEditPage()
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
    private fun setButtons() {
        binding.operationTypeFormActivityAddButton.setOnClickListener {
            add()
        }
        binding.operationTypeFormActivityBackButton.setOnClickListener {
            exit()
        }
    }

    private fun setEditPage() {
        isEdit = intent.extras?.getBoolean("isEdit", false)!!
        if (isEdit) {
            binding.operationTypeFormActivityTitleEditText.setText(intent.extras?.getString("title")!!)
        }

        if (isEdit) {
            if (isExpense) {
                binding.operationTypeFormActivityTitle.text = resources.getText(R.string.edit_expense_type)
            } else {
                binding.operationTypeFormActivityTitle.text = resources.getText(R.string.edit_earning_type)
            }
            binding.operationTypeFormActivityAddButton.text = binding.operationTypeFormActivityTitle.text
        }
    }

    private fun setTheme() {
        isExpense = intent.extras?.getBoolean("isExpense", false)!!

        val textColor: Int
        val textSecondary: Int
        val backgroundColor: Int
        val mainColor: Int

        binding.apply {
            if (BudgetDataApi.mode.value!! == UserFragment.DAY) {
                textColor = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    R.color.text_primary
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    R.color.text_secondary
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    R.color.light_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    if (isExpense) R.color.red_main else R.color.blue_main
                )
            } else {
                textColor = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    R.color.light_grey
                )
                textSecondary = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    R.color.grey
                )
                backgroundColor = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    R.color.dark_grey
                )
                mainColor = ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    R.color.darker_grey
                )
            }
            operationTypeFormActivityTitle.text =
                resources.getText(if (isExpense) R.string.add_expense_type else R.string.add_earning_type)
            operationTypeFormActivityAddButton.text =
                resources.getText(if (isExpense) R.string.add_expense_type else R.string.add_earning_type)

            operationTypeFormActivityHeaderLayout.setBackgroundColor(mainColor)
            operationTypeFormActivityAddButton.backgroundTintList = ColorStateList.valueOf(mainColor)
            operationTypeFormActivityBackButton.backgroundTintList = ColorStateList.valueOf(mainColor)
            operationTypeFormActivityLayout.setBackgroundColor(backgroundColor)


            operationTypeFormActivityTitleEditText.backgroundTintList = ColorStateList.valueOf(mainColor)

            operationTypeFormActivityTitleEditText.highlightColor = mainColor
            operationTypeFormActivityTitleEditText.setLinkTextColor(mainColor)
            operationTypeFormActivityTitleEditText.setTextColor(textColor)
            operationTypeFormActivityTitleEditText.setHintTextColor(textSecondary)
        }
    }

    //endregion
    //region Methods
    private fun appear() {
        binding.operationTypeFormActivityTitleEditText.startAnimation(appear)
        binding.operationTypeFormActivityHeaderLayout.startAnimation(appear)
        binding.operationTypeFormActivityAddButton.startAnimation(appear)
    }

    private fun checkFields(): Boolean {
        var isValid = true
        binding.operationTypeFormActivityTitleEditText.apply {
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
            intent.putExtra("title", binding.operationTypeFormActivityTitleEditText.text.toString())
            if (isEdit) intent.putExtra("isEdit", true)
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