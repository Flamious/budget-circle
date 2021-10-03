package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doOnTextChanged
import com.example.budgetcircle.R
import com.example.budgetcircle.databinding.ActivityExpensesFormBinding
import com.example.budgetcircle.viewmodel.items.HistoryItem
import java.util.*

class ExpensesFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpensesFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesFormBinding.inflate(layoutInflater)
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.expSum.doOnTextChanged { text, _, _, _ ->
            if (text.toString().toFloatOrNull() == null) {
                binding.expAddButton.apply {
                    isEnabled = false
                }
            }
            else {
                binding.expAddButton.apply {
                    isEnabled = true
                }
            }
        }
        binding.expAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun add() {
        val intent = Intent()
        intent.putExtra("sum", binding.expSum.text.toString().toFloat())
        intent.putExtra("type", "Food")
        intent.putExtra("isRep", binding.expRepSwitch.isChecked)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}