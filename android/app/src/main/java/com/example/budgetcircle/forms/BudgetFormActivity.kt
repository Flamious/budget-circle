package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityBudgetFormBinding

class BudgetFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetFormBinding
    var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetFormBinding.inflate(layoutInflater)
        setButtons()

        isEdit = intent.getStringExtra("edit") != null
        if (isEdit) {
            binding.apply {
                accName.setText(intent.getStringExtra("accountName")!!)
                budgetSum.setText(intent.getFloatExtra("newAccountBudget", 0f).toString())
                budgetAddButton.text = resources.getText(R.string.edit_account)
                budgetFormTitle.text = resources.getText(R.string.edit_account)
            }
        }

        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.accName.doOnTextChanged { _, _, _, _ ->
            check()
        }
        binding.budgetSum.doOnTextChanged { _, _, _, _ ->
            check()
        }
        binding.budgetAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun check() {
        var sum = binding.budgetSum.text.toString().toFloatOrNull()
        binding.budgetAddButton.isEnabled =
            !(sum == null || sum <= 0f || binding.accName.text.isNullOrBlank())
    }

    private fun add() {
        val intent = Intent()
        intent.putExtra("type", if (isEdit) "editAccount" else "newAccount")
        intent.putExtra("newAccountName", binding.accName.text.toString())
        intent.putExtra("newAccountBudget", binding.budgetSum.text.toString().toFloat())
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}