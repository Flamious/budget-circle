package com.example.budgetcircle

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.budgetcircle.databinding.ActivityMainBinding
import com.example.budgetcircle.fragments.*
import com.example.budgetcircle.fragments.history.HistoryFragment
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.BudgetDataApi

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val budgetDataApi: BudgetDataApi by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.token = "Bearer ${intent.extras?.getString("token")!!}"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initiateViewModel()
        setNavMenu()
        applyDayNight()
    }

    override fun onBackPressed() {

    }

    //region Setting
    private fun initiateViewModel() {
        budgetDataApi.expensesDateString.postValue(resources.getString(R.string.week))
        budgetDataApi.earningsDateString.postValue(resources.getString(R.string.week))
        budgetDataApi.operationListDateString.postValue(resources.getString(R.string.week))
        budgetDataApi.operationType.postValue(resources.getString(R.string.all))
        budgetDataApi.operationListStartWith.postValue(resources.getString(R.string.start_with_new))
        budgetDataApi.operationListChosenBudgetTypeString.postValue(resources.getString(R.string.all))
        budgetDataApi.operationChartChosenBudgetTypeString.postValue(resources.getString(R.string.all))
        budgetDataApi.operationListChosenTypeString.postValue(resources.getString(R.string.all))
        budgetDataApi.chartOperationPeriod.postValue(resources.getString(R.string.week))
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
                        OperationFragment(false)
                    }
                    R.id.expenses -> {
                        setNavColor(R.color.nav_red_selector)
                        OperationFragment(true)
                    }
                    R.id.budget -> {
                        setNavColor(R.color.nav_green_selector)
                        BudgetFragment()
                    }
                    R.id.history -> {
                        setNavColor(R.color.nav_orange_selector)
                        HistoryFragment()
                    }
                    R.id.userSettings -> {
                        setNavColor(R.color.nav_purple_selector)
                        UserFragment()
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
        val navColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                if (Settings.isDay()) color else R.color.grey
            )
        )
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

    fun applyDayNight() {
        val backgroundNavColor: Int
        if (Settings.isDay()) {
            backgroundNavColor = ContextCompat.getColor(
                this@MainActivity,
                R.color.white
            )

            if (binding.navigationMenu.selectedItemId == R.id.userSettings)
                setNavColor(R.color.purple_main)
        } else {
            backgroundNavColor = ContextCompat.getColor(
                this@MainActivity,
                R.color.darker_grey
            )

            setNavColor(R.color.grey)
        }

        binding.navigationMenu.backgroundTintList = ColorStateList.valueOf(backgroundNavColor)
    }
    //endregion
}