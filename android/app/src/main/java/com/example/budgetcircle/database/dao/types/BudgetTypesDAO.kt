package com.example.budgetcircle.database.dao.types

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.types.BudgetType

@Dao
interface BudgetTypesDAO {
    @Query("SELECT * FROM budget_types WHERE id = :id")
    fun getById(id: Int): BudgetType

    @Query("SELECT sum FROM budget_types")
    fun getSums(): List<Float>

    @Insert
    fun insert(types: BudgetType)

    @Insert
    fun insertAll(vararg types: BudgetType)

    @Update
    fun update(types: BudgetType)

    @Query("DELETE FROM budget_types WHERE id = :id AND isDeletable")
    fun delete(id: Int)

    @Query("DELETE FROM budget_types")
    fun deleteAll()

    @Query("SELECT * FROM budget_types")
    fun getAll(): LiveData<List<BudgetType>>
}