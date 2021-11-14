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
                    db.execSQL("INSERT INTO budget_types (title, titleRu, sum, isDeletable) VALUES ('Cash', 'Наличные', 0.0, 0)")
                    db.execSQL("INSERT INTO budget_types (title, titleRu, sum, isDeletable) VALUES ('Bank', 'Банк', 0.0, 0)")
                    db.execSQL("INSERT INTO budget_types (title, titleRu, sum, isDeletable) VALUES ('Card 1', 'Карта 1', 0.0, 1)")
                    db.execSQL("INSERT INTO budget_types (title, titleRu, sum, isDeletable) VALUES ('Card 2', 'Карта 2', 0.0, 1)")

                    db.execSQL("INSERT INTO expenses_types (title, titleRu) VALUES ('Home', 'Дом')")
                    db.execSQL("INSERT INTO expenses_types (title, titleRu) VALUES ('Food', 'Еда')")
                    db.execSQL("INSERT INTO expenses_types (title, titleRu) VALUES ('Debts', 'Долги')")
                    db.execSQL("INSERT INTO expenses_types (title, titleRu) VALUES ('Transport', 'Транспорт')")
                    db.execSQL("INSERT INTO expenses_types (title, titleRu) VALUES ('Bills and services', 'счета и налоги')")
                    db.execSQL("INSERT INTO expenses_types (title, titleRu) VALUES ('Personal expenses', 'Личные расходы')")
                    db.execSQL("INSERT INTO expenses_types (title, titleRu) VALUES ('Other', 'Другое')")

                    db.execSQL("INSERT INTO earning_types (title, titleRu) VALUES ('Salary', 'Зарплата')")
                    db.execSQL("INSERT INTO earning_types (title, titleRu) VALUES ('Pensions and scholarship', 'Пенсии и стипендии')")
                    db.execSQL("INSERT INTO earning_types (title, titleRu) VALUES ('Real estate', 'Недвижимость')")
                    db.execSQL("INSERT INTO earning_types (title, titleRu) VALUES ('Investments', 'Инвестиции')")
                    db.execSQL("INSERT INTO earning_types (title, titleRu) VALUES ('Business', 'Бизнесс')")
                    db.execSQL("INSERT INTO earning_types (title, titleRu) VALUES ('Prizes', 'Премии')")
                    db.execSQL("INSERT INTO earning_types (title, titleRu) VALUES ('Other', 'Другое')")
                }
            }
        }
    }
}