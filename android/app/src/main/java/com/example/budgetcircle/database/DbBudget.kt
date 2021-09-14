package com.example.budgetcircle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgetcircle.database.dao.main.EarningsDAO
import com.example.budgetcircle.database.dao.main.ExpensesDAO
import com.example.budgetcircle.database.dao.main.SavingsDAO
import com.example.budgetcircle.database.dao.types.SavingTypesDAO
import com.example.budgetcircle.database.entities.main.BudgetSaving
import com.example.budgetcircle.database.entities.main.Earning
import com.example.budgetcircle.database.entities.main.Expense
import com.example.budgetcircle.database.entities.types.SavingType

@Database(entities = [
    BudgetSaving::class, SavingType::class,
    Earning::class, Expense::class
], version = 1, exportSchema = false)
abstract class DbBudget : RoomDatabase() {
    abstract fun EarningsDAO() : EarningsDAO
    abstract fun ExpensesDAO() : ExpensesDAO
    abstract fun SavingsDAO() : SavingsDAO
    abstract fun SavingTypesDAO() : SavingTypesDAO

    companion object {
        @Volatile
        private var INSTANCE: DbBudget? = null

        fun getDatabase(context: Context) : DbBudget {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DbBudget::class.java,
                    "budget_database",
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}