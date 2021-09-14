package com.example.budgetcircle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.budgetcircle.databinding.ActivityMainBinding
import com.example.budgetcircle.fragments.*
import com.example.budgetcircle.viewmodel.BudgetData

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val budgetdata: BudgetData by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationMenu.selectedItemId = R.id.budget
        supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, BudgetFragment()).commit()

        binding.navigationMenu.setOnItemSelectedListener {
            openFragment(when (it.itemId) {
                R.id.earnings -> {
                    EarningsFragment()
                }
                R.id.expenses -> {
                    ExpensesFragment()
                }
                R.id.budget -> {
                    BudgetFragment()
                }
                R.id.history -> {
                    HistoryFragment()
                }
                R.id.settings -> {
                    SettingsFragment()
                }
                else -> {
                    BudgetFragment()
                }
            })
            true
        }

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPanel, fragment)
            .commit()
    }
}