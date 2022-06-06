package com.example.budgetcircle.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.BudgetType

@Dao
interface BudgetTypeDao {
    @Query("SELECT * FROM budget_types")
    fun getAll(): LiveData<List<BudgetType>>

    @Insert
    fun insertMany(types: List<BudgetType>)

    @Query("DELETE FROM budget_types WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM budget_types")
    fun deleteAll()
}