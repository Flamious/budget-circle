package com.example.budgetcircle.database.dao.main

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.main.BudgetSaving

@Dao
interface SavingsDAO {
    @Insert
    suspend fun insert(item: BudgetSaving)

    @Update
    suspend fun update(item: BudgetSaving)

    @Query("DELETE FROM savings WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM savings")
    fun getAll(): LiveData<List<BudgetSaving>>
}