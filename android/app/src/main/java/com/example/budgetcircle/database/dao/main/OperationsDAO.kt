package com.example.budgetcircle.database.dao.main

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.budgetcircle.database.entities.main.Operation
import com.example.budgetcircle.database.entities.main.OperationSum
import java.util.*

@Dao
interface OperationsDAO {
    @Insert
    suspend fun insert(item: Operation)

    @Update
    suspend fun update(item: Operation)

    @Query("DELETE FROM operations WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM operations WHERE budgetTypeId = :id")
    fun deleteByBudgetTypeId(id: Int)

    @Query("SELECT types.title, SUM(e.sum) as sum FROM operations AS e JOIN earning_types AS types ON e.typeId == types.id AND NOT e.isExpense GROUP BY types.title")
    fun getAllEarnings(): LiveData<List<OperationSum>>

    @Query("SELECT types.title, SUM(e.sum) as sum FROM operations AS e JOIN earning_types AS types ON e.typeId == types.id AND e.date >= :date AND NOT e.isExpense GROUP BY types.title")
    fun getAllEarningsByDate(date: Date): LiveData<List<OperationSum>>

    @Query("SELECT types.title, SUM(e.sum) as sum FROM operations AS e JOIN expenses_types AS types ON e.typeId == types.id AND e.isExpense GROUP BY types.title")
    fun getAllExpenses(): LiveData<List<OperationSum>>

    @Query("SELECT types.title, SUM(e.sum) as sum FROM operations AS e JOIN expenses_types AS types ON e.typeId == types.id AND e.date >= :date AND e.isExpense GROUP BY types.title")
    fun getAllExpensesByDate(date: Date): LiveData<List<OperationSum>>

    @Query("SELECT * FROM operations")
    fun getAllHistoryItems(): LiveData<List<Operation>>

    @Query("SELECT sum FROM operations WHERE NOT isExpense")
    fun getEarningsSums(): List<Double>

    @Query("SELECT sum FROM operations WHERE isExpense")
    fun getExpensesSums(): List<Double>

    @Query("SELECT * FROM operations WHERE id = :id")
    fun getById(id: Int): Operation

}