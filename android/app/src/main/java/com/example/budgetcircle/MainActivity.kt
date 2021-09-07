package com.example.budgetcircle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.budgetcircle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationMenu.selectedItemId = R.id.budget
        supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, BudgetFragment()).commit()

        binding.navigationMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.earnings -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, EarningsFragment()).commit()
                }
                R.id.expenses -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, ExpensesFragment()).commit()
                }
                R.id.budget -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, BudgetFragment()).commit()
                }
                R.id.history -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, HistoryFragment()).commit()
                }
                R.id.settings -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, SettingsFragment()).commit()
                }
            }
            true
        }
    }
}