package com.example.budgetcircle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.budgetcircle.database.converters.DateConverter
import com.example.budgetcircle.database.dao.main.EarningsDAO
import com.example.budgetcircle.database.dao.main.ExpensesDAO
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.dao.types.EarningTypesDAO
import com.example.budgetcircle.database.dao.types.ExpenseTypesDAO
import com.example.budgetcircle.database.entities.main.Earning
import com.example.budgetcircle.database.entities.main.Expense
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.entities.types.EarningType
import com.example.budgetcircle.database.entities.types.ExpenseType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BudgetType::class,
        Earning::class, Expense::class,
        ExpenseType::class, EarningType::class
    ], version = 1, exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class DbBudget : RoomDatabase() {
    abstract fun EarningsDAO(): EarningsDAO
    abstract fun ExpensesDAO(): ExpensesDAO
    abstract fun BudgetTypesDAO(): BudgetTypesDAO
    abstract fun ExpenseTypesDAO(): ExpenseTypesDAO
    abstract fun EarningTypesDAO(): EarningTypesDAO

    companion object {
        @Volatile
        private var INSTANCE: DbBudget? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DbBudget {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DbBudget::class.java,
                    "budget_database",
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(BudgetDbCallback(scope))
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class BudgetDbCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDb(
                        database.EarningTypesDAO(),
                        database.ExpenseTypesDAO(),
                        database.BudgetTypesDAO()
                    )
                }
            }
        }

        fun populateDb(
            earningTypesDAO: EarningTypesDAO,
            expenseTypesDAO: ExpenseTypesDAO,
            budgetTypesDAO: BudgetTypesDAO
        ) {
            expenseTypesDAO.insert(
                ExpenseType("Home"),
                ExpenseType("Food"),
                ExpenseType("Debts"),
                ExpenseType("Transport"),
                ExpenseType("Bills and services"),
                ExpenseType("Personal expenses"),
                ExpenseType("Other")
            )
            earningTypesDAO.insert(
                EarningType("Salary"),
                EarningType("Pensions and scholarship"),
                EarningType("Real estate"),
                EarningType("Investments"),
                EarningType("Business"),
                EarningType("Prizes"),
                EarningType("Other")
            )
            budgetTypesDAO.insertAll(
                BudgetType("Cash", 0f, false),
                BudgetType("Bank", 0f, false),
                BudgetType("Card 1", 0f, true),
                BudgetType("Card 2", 0f, true)
            )
        }
    }


}