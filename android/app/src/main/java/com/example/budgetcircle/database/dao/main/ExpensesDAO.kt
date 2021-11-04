package com.example.budgetcircle.database.dao.main

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.main.Expense
import com.example.budgetcircle.database.entities.main.OperationSum
import java.util.*

@Dao
interface ExpensesDAO {
    @Query("SELECT sum FROM expenses")
    fun getSums(): List<Double>

    @Insert
    suspend fun insert(item: Expense)

    @Update
    suspend fun update(item: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM expenses WHERE budgetTypeId = :id")
    fun deleteByBudgetTypeId(id: Int)

    @Query("SELECT types.title, SUM(e.sum) as sum FROM expenses AS e JOIN expenses_types AS types ON e.typeId == types.id GROUP BY types.title")
    fun getAll(): LiveData<List<OperationSum>>

    @Query("SELECT types.title, SUM(e.sum) as sum FROM expenses AS e JOIN expenses_types AS types ON e.typeId == types.id AND e.date >= :date GROUP BY types.title")
    fun getAllByDate(date: Date): LiveData<List<OperationSum>>
}