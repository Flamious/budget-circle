package com.example.budgetcircle

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.budgetcircle.databinding.ActivityMainBinding
import com.example.budgetcircle.fragments.*
import com.example.budgetcircle.fragments.history.HistoryFragment
import com.example.budgetcircle.fragments.settings.SettingsFragment
import com.example.budgetcircle.viewmodel.BudgetDataApi

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val budgetDataApi: BudgetDataApi by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Token = "Bearer ${intent.extras?.getString("token")!!}"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initiateViewModel()
        setNavMenu()
        Toast.makeText(this, Token, Toast.LENGTH_LONG).show()
    }
    //region Setting
    private fun initiateViewModel() {
        budgetDataApi.expensesDateString.postValue(resources.getString(R.string.week))
        budgetDataApi.earningsDateString.postValue(resources.getString(R.string.week))
    }

    private fun setNavMenu() {
        binding.navigationMenu.selectedItemId = R.id.budget
        setNavColor(R.color.nav_green_selector)
        openFragment(BudgetFragment())
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

    private fun setNavColor(color: Int) {
        val navColor = ColorStateList.valueOf(ContextCompat.getColor(this, color))
        val colorSecondary =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.no_money_op))
        binding.navigationMenu.itemTextColor = navColor
        binding.navigationMenu.itemIconTintList = navColor
        binding.navigationMenu.itemRippleColor = colorSecondary
    }
    //endregion
    //region Methods
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPanel, fragment)
            .commit()
    }
    //endregion

    companion object {
        var Token: String = ""
    }
}