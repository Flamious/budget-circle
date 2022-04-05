package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityBudgetFormBinding
import com.example.budgetcircle.settings.SumInputFilter

class BudgetFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetFormBinding
    private var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetFormBinding.inflate(layoutInflater)
        setInitialValues()
        setButtons()
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        exit()
    }
    //region Setting
    private fun setButtons() {
        binding.budgetAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun setInitialValues() {
        isEdit = intent.getStringExtra("edit") != null
        binding.budgetSum.filters = arrayOf<InputFilter>(SumInputFilter())
        if (isEdit) {
            binding.apply {
                accName.setText(intent.getStringExtra("accountName")!!)
                budgetSum.setText(intent.getDoubleExtra("newAccountBudget", 0.0).toString())
                budgetAddButton.text = resources.getText(R.string.edit_account)
                budgetFormTitle.text = resources.getText(R.string.edit_account)
            }
        }
    }
    //endregion
    //region Methods
    private fun checkFields(): Boolean {
        val sum = binding.budgetSum.text.toString().toDoubleOrNull()
        var isValid = true
        binding.budgetSum.apply {
            error = null
            if (sum == null) {
                error = resources.getString(R.string.empty_field)
                isValid = false
            }
        }
        binding.accName.apply {
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
            intent.putExtra("newAccountName", binding.accName.text.toString())
            intent.putExtra("newAccountBudget", binding.budgetSum.text.toString().toDouble())
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