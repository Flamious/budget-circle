package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.budgetcircle.databinding.ActivityBudgetExchangeBinding

class BudgetExchangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetExchangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetExchangeBinding.inflate(layoutInflater)
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.budgetExchangeAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun add() {
        val intent = Intent()
        intent.putExtra("Ha", "Hi! I'm just a little toast")
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}