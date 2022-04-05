package com.example.budgetcircle.forms

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityOperationTypeFormBinding


class OperationTypeFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityOperationTypeFormBinding
    private var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperationTypeFormBinding.inflate(layoutInflater)
        setTheme()
        setEditPage()
        setButtons()
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        exit()
    }
    //region Setting
    private fun setButtons() {
        binding.opTypeAddButton.setOnClickListener {
            add()
        }
        binding.opTypeBackButton.setOnClickListener {
            exit()
        }
    }

    private fun setEditPage() {
        isEdit = intent.extras?.getBoolean("isEdit", false)!!
        if (isEdit) {
            binding.opTitle.setText(intent.extras?.getString("title")!!)
        }
    }

    private fun setTheme() {
        val isExpense = intent.extras?.getBoolean("isExpense", false)!!
        val mainColor = if (isExpense) R.color.red_main else R.color.blue_main
        val secondaryColor = if (isExpense) R.color.red_secondary else R.color.blue_secondary

        binding.opTypeTitle2.text =
            resources.getText(if (isExpense) R.string.add_expense_type else R.string.add_earning_type)
        binding.opTypeAddButton.text =
            resources.getText(if (isExpense) R.string.add_expense_type else R.string.add_earning_type)
        binding.apply {
            opTypeAddButton.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    mainColor
                )
            )
            opTitle.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    mainColor
                )
            )
            opTitle.highlightColor =
                ContextCompat.getColor(this@OperationTypeFormActivity, mainColor)
            opTitle.setLinkTextColor(
                ContextCompat.getColor(
                    this@OperationTypeFormActivity,
                    secondaryColor
                )
            )
        }
    }

    //endregion
    //region Methods
    private fun checkFields(): Boolean {
        var isValid = true
        binding.opTitle.apply {
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
            intent.putExtra("title", binding.opTitle.text.toString())
            if(isEdit) intent.putExtra("isEdit", true)
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