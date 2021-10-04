package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityExpensesFormBinding
import com.example.budgetcircle.dialogs.Dialogs

class ExpensesFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpensesFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesFormBinding.inflate(layoutInflater)
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.expSum.doOnTextChanged { _, _, _, _ ->
            check()
        }
        binding.expTitle.doOnTextChanged { _, _, _, _ ->
            check()
        }
        binding.dateLayout.setOnClickListener {
            Dialogs().pickDate(
                this,
                binding.expDate,
                R.style.redColorDatePicker
            )
        }
        binding.kindLayout.setOnClickListener {
            Dialogs().chooseOne(
                this,
                resources.getString(R.string.kind),
                resources.getStringArray(R.array.expense_titles),
                binding.selectKind
            )
        }
        binding.expAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun check() {
        var sum = binding.expSum.text.toString().toFloatOrNull()
        if (sum == null || sum <= 0f || binding.expTitle.text.isNullOrBlank()) {
            binding.expAddButton.apply {
                isEnabled = false
            }
        } else {
            binding.expAddButton.apply {
                isEnabled = true
            }
        }
    }

    private fun add() {
        val intent = Intent()
        intent.putExtra("sum", binding.expSum.text.toString().toFloat())
        intent.putExtra("type", binding.selectKind.text.toString())
        intent.putExtra("isRep", binding.expRepSwitch.isChecked)
        intent.putExtra("title", binding.expTitle.text.toString())
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}