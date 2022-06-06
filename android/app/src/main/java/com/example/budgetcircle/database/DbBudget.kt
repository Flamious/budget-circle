package com.example.budgetcircle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.budgetcircle.database.dao.OperationDao
import com.example.budgetcircle.database.dao.BudgetTypeDao
import com.example.budgetcircle.database.dao.EarningTypeDao
import com.example.budgetcircle.database.dao.ExpenseTypeDao
import com.example.budgetcircle.database.entities.Operation
import com.example.budgetcircle.database.entities.BudgetType
import com.example.budgetcircle.database.entities.EarningType
import com.example.budgetcircle.database.entities.ExpenseType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        BudgetType::class,
        ExpenseType::class,
        EarningType::class,
        Operation::class
    ], version = 4, exportSchema = false
)

abstract class DbBudget : RoomDatabase() {
    abstract fun BudgetTypesDAO(): BudgetTypeDao
    abstract fun ExpenseTypesDAO(): ExpenseTypeDao
    abstract fun EarningTypesDAO(): EarningTypeDao
    abstract fun OperationsDAO(): OperationDao

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
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}