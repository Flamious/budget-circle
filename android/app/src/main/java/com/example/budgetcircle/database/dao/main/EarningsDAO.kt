package com.example.budgetcircle.database.dao.main

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.main.Earning
import com.example.budgetcircle.database.entities.main.OperationSum
import java.util.*

@Dao
interface EarningsDAO {
    @Query("SELECT sum FROM earnings")
    fun getSums(): List<Float>

    @Insert
    suspend fun insert(item: Earning)

    @Update
    suspend fun update(item: Earning)

    @Query("DELETE FROM earnings WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM earnings WHERE budgetTypeId = :id")
    fun deleteByBudgetTypeId(id: Int)

    @Query("SELECT types.title, SUM(e.sum) as sum FROM earnings AS e JOIN earning_types AS types ON e.typeId == types.id GROUP BY types.title")
    fun getAll(): LiveData<List<OperationSum>>

    @Query("SELECT types.title, SUM(e.sum) as sum FROM earnings AS e JOIN earning_types AS types ON e.typeId == types.id AND e.date >= :date GROUP BY types.title")
    fun getAllByDate(date: Date): LiveData<List<OperationSum>>

}