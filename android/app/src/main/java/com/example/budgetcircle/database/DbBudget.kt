package com.example.budgetcircle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.budgetcircle.database.converters.DateConverter
import com.example.budgetcircle.database.dao.main.OperationsDAO
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.dao.types.EarningTypesDAO
import com.example.budgetcircle.database.dao.types.ExpenseTypesDAO
import com.example.budgetcircle.database.entities.main.Operation
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.entities.types.EarningType
import com.example.budgetcircle.database.entities.types.ExpenseType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        BudgetType::class,
        ExpenseType::class,
        EarningType::class,
        Operation::class
    ], version = 2, exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class DbBudget : RoomDatabase() {
    abstract fun BudgetTypesDAO(): BudgetTypesDAO
    abstract fun ExpenseTypesDAO(): ExpenseTypesDAO
    abstract fun EarningTypesDAO(): EarningTypesDAO
    abstract fun OperationsDAO(): OperationsDAO

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
            INSTANCE?.let {
                scope.launch {
                    db.execSQL("INSERT INTO budget_types (title, sum, isDeletable) VALUES ('Cash', 0.0, 0)")
                    db.execSQL("INSERT INTO budget_types (title, sum, isDeletable) VALUES ('Bank', 0.0, 0)")
                    db.execSQL("INSERT INTO budget_types (title, sum, isDeletable) VALUES ('Card 1', 0.0, 1)")
                    db.execSQL("INSERT INTO budget_types (title, sum, isDeletable) VALUES ('Card 2', 0.0, 1)")

                    db.execSQL("INSERT INTO expenses_types (title) VALUES ('Home')")
                    db.execSQL("INSERT INTO expenses_types (title) VALUES ('Food')")
                    db.execSQL("INSERT INTO expenses_types (title) VALUES ('Debts')")
                    db.execSQL("INSERT INTO expenses_types (title) VALUES ('Transport')")
                    db.execSQL("INSERT INTO expenses_types (title) VALUES ('Bills and services')")
                    db.execSQL("INSERT INTO expenses_types (title) VALUES ('Personal expenses')")
                    db.execSQL("INSERT INTO expenses_types (title) VALUES ('Other')")

                    db.execSQL("INSERT INTO earning_types (title) VALUES ('Salary')")
                    db.execSQL("INSERT INTO earning_types (title) VALUES ('Pensions and scholarship')")
                    db.execSQL("INSERT INTO earning_types (title) VALUES ('Real estate')")
                    db.execSQL("INSERT INTO earning_types (title) VALUES ('Investments')")
                    db.execSQL("INSERT INTO earning_types (title) VALUES ('Business')")
                    db.execSQL("INSERT INTO earning_types (title) VALUES ('Prizes')")
                    db.execSQL("INSERT INTO earning_types (title) VALUES ('Other')")
                }
            }
        }
    }
}