package com.example.budgetcircle.database.dao.types

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.types.ExpenseType

@Dao
interface ExpenseTypesDAO {
    @Insert
    fun insert(vararg types: ExpenseType)

    @Update
    fun update(types: ExpenseType)

    @Query("DELETE FROM expenses_types WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM expenses_types")
    fun deleteAll()

    @Query("SELECT * FROM budget_types")
    fun getAll(): List<ExpenseType>
}