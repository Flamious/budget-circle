package com.example.budgetcircle.forms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.budgetcircle.databinding.ActivityEarningsFormBinding

class EarningsFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityEarningsFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningsFormBinding.inflate(layoutInflater)
        setButtons()
        setContentView(binding.root)
    }

    private fun setButtons() {
        binding.earnAddButton.setOnClickListener {
            add()
        }
        binding.backButton.setOnClickListener {
            exit()
        }
    }

    private fun add() {
        val intent = Intent()
        intent.putExtra("sum", binding.earnSum.text.toString().toFloat())
        intent.putExtra("type", "Business")
        intent.putExtra("isRep", binding.earnRepSwitch.isChecked)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun exit() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}