package com.example.budgetcircle

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
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
        val color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_green_selector))
        binding.navigationMenu.itemTextColor = color
        binding.navigationMenu.itemIconTintList = color
        supportFragmentManager.beginTransaction().replace(R.id.fragmentPanel, BudgetFragment()).commit()

        binding.navigationMenu.setOnItemSelectedListener {
            val color: ColorStateList
            openFragment(when (it.itemId) {
                R.id.earnings -> {
                    color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_blue_selector))
                    EarningsFragment()
                }
                R.id.expenses -> {
                    color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_red_selector))
                    ExpensesFragment()
                }
                R.id.budget -> {
                    color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_green_selector))
                    BudgetFragment()
                }
                R.id.history -> {
                    color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_green_selector))
                    HistoryFragment()
                }
                R.id.settings -> {
                    color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_green_selector))
                    SettingsFragment()
                }

                else -> {
                    color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_green_selector))
                    BudgetFragment()
                }
            })
            binding.navigationMenu.itemTextColor = color
            binding.navigationMenu.itemIconTintList = color
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