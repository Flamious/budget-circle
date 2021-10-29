package com.example.budgetcircle.database.dao.types

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.types.ExpenseType

@Dao
interface ExpenseTypesDAO {
    @Insert
    suspend fun insert(types: ExpenseType)

    @Update
    suspend fun update(types: ExpenseType)

    @Query("DELETE FROM expenses_types WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM budget_types")
    fun getAll(): LiveData<List<ExpenseType>>
}