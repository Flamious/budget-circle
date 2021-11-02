package com.example.budgetcircle.database.dao.main

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.main.Expense

@Dao
interface ExpensesDAO {
    @Query("SELECT sum FROM expenses")
    fun getSums(): List<Float>

    @Insert
    suspend fun insert(item: Expense)

    @Update
    suspend fun update(item: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM expenses WHERE budgetTypeId = :id")
    fun deleteByBudgetTypeId(id: Int)

    @Query("SELECT * FROM expenses")
    fun getAll(): LiveData<List<Expense>>
}