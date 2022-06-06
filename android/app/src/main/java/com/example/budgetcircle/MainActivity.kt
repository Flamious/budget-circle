package com.example.budgetcircle

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.budgetcircle.database.entities.BudgetType
import com.example.budgetcircle.database.entities.EarningType
import com.example.budgetcircle.database.entities.ExpenseType
import com.example.budgetcircle.database.entities.Operation
import com.example.budgetcircle.databinding.ActivityMainBinding
import com.example.budgetcircle.fragments.*
import com.example.budgetcircle.fragments.history.HistoryFragment
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.BudgetCircleData
import com.example.budgetcircle.viewmodel.LocalData

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val budgetCircleData: BudgetCircleData by viewModels()
    private val localData: LocalData by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.token = "Bearer ${intent.extras?.getString("token")!!}"
        Settings.login = intent.extras?.getString("login")!!
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initiateViewModel()
        setNavMenu()
        applyDayNight()
        setObservation()
    }

    override fun onBackPressed() {

    }

    //region Setting
    private fun initiateViewModel() {
        budgetCircleData.expensesDateString.postValue(resources.getString(R.string.week))
        budgetCircleData.earningsDateString.postValue(resources.getString(R.string.week))
        budgetCircleData.operationListDateString.postValue(resources.getString(R.string.week))
        budgetCircleData.operationType.postValue(resources.getString(R.string.all))
        budgetCircleData.operationListStartWith.postValue(resources.getString(R.string.start_with_new))
        budgetCircleData.operationListChosenBudgetTypeString.postValue(resources.getString(R.string.all))
        budgetCircleData.operationChartChosenBudgetTypeString.postValue(resources.getString(R.string.all))
        budgetCircleData.operationListChosenTypeString.postValue(resources.getString(R.string.all))
        budgetCircleData.chartOperationPeriod.postValue(resources.getString(R.string.week))
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

    fun setObservation() {
        budgetCircleData.budgetTypes.observe(this) {
            if (it != null)
                localData.postBudgetTypes(List(it.size) { index ->
                    BudgetType(
                        it[index].id,
                        it[index].title,
                        it[index].sum
                    )
                })
        }
        budgetCircleData.earningTypes.observe(this) {
            if (it != null)
                localData.postEarningTypes(List(it.size) { index ->
                    EarningType(
                        it[index].id,
                        it[index].title,
                        it[index].sum
                    )
                })
        }
        budgetCircleData.expenseTypes.observe(this) {
            if (it != null)
                localData.postExpenseTypes(List(it.size) { index ->
                    ExpenseType(
                        it[index].id,
                        it[index].title,
                        it[index].sum
                    )
                })
        }
        localData.operations.observe(this) {
            if (it.isNotEmpty()) {
                synchronizeData(it)
            }
        }
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

    private fun synchronizeData(operations: List<Operation>) {
        val list = List(operations.size) { index ->
            com.example.budgetcircle.viewmodel.models.Operation(
                -1,
                operations[index].title,
                operations[index].sum,
                "",
                operations[index].typeId,
                operations[index].budgetTypeId,
                operations[index].commentary,
                operations[index].isExpense
            )
        }

        budgetCircleData.addManyOperation(list)

        for (operation in operations) {
            localData.deleteOperation(operation.id)
        }
    }
    //endregion
}