package com.example.budgetcircle

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.budgetcircle.databinding.ActivityExpensesFormBinding
import com.example.budgetcircle.databinding.ActivityMainBinding
import com.example.budgetcircle.fragments.*
import com.example.budgetcircle.fragments.history.HistoryFragment
import com.example.budgetcircle.fragments.settings.SettingsFragment
import com.example.budgetcircle.viewmodel.BudgetData

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val budgetData: BudgetData by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.navigationMenu.selectedItemId = R.id.budget
        setNavColor(R.color.nav_green_selector)
        openFragment(BudgetFragment())

        budgetData.expensesDateString.postValue(resources.getString(R.string.week))
        budgetData.earningsDateString.postValue(resources.getString(R.string.week))
        budgetData.budgetTypes.observe(this, { }) //TODO убрать костыль, для инициализации списка счетов
        binding.navigationMenu.setOnItemSelectedListener {
            openFragment(
                when (it.itemId) {
                    R.id.earnings -> {
                        setNavColor(R.color.nav_blue_selector)
                        EarningsFragment()
                    }
                    R.id.expenses -> {
                        setNavColor(R.color.nav_red_selector)
                        ExpensesFragment()
                    }
                    R.id.budget -> {
                        setNavColor(R.color.nav_green_selector)
                        BudgetFragment()
                    }
                    R.id.history -> {
                        setNavColor(R.color.nav_orange_selector)
                        HistoryFragment()
                    }
                    R.id.settings -> {
                        setNavColor(R.color.nav_purple_selector)
                        SettingsFragment()
                    }

                    else -> {
                        setNavColor(R.color.nav_green_selector)
                        BudgetFragment()
                    }
                }
            )
            true
        }

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPanel, fragment)
            .commit()
    }

    private fun setNavColor(color: Int) {
        val color = ColorStateList.valueOf(ContextCompat.getColor(this, color))
        val colorSecondary =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.no_money_op))
        binding.navigationMenu.itemTextColor = color
        binding.navigationMenu.itemIconTintList = color
        binding.navigationMenu.itemRippleColor = colorSecondary
    }
}