package com.example.budgetcircle.database.dao.types

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.types.BudgetType

@Dao
interface BudgetTypesDAO {
    @Insert
    suspend fun insert(types: BudgetType)

    @Update
    suspend fun update(types: BudgetType)

    @Query("DELETE FROM budget_types WHERE id = :id AND isDeletable")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM budget_types")
    fun getAll(): LiveData<List<BudgetType>>
}