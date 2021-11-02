package com.example.budgetcircle.database.dao.main

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.main.Earning

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

    @Query("SELECT * FROM earnings")
    fun getAll(): LiveData<List<Earning>>
}