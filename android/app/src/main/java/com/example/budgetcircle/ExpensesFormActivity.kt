package com.example.budgetcircle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.budgetcircle.databinding.ActivityExpensesFormBinding

class ExpensesFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpensesFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesFormBinding.inflate(layoutInflater)
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.expAddButton.setOnClickListener() {
            add()
        }
        binding.backButton.setOnClickListener() {
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