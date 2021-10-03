package com.example.budgetcircle.database.dao.main

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.main.Earning

@Dao
interface EarningsDAO {
    @Insert
    suspend fun insert(item: Earning)

    @Update
    suspend fun update(item: Earning)

    @Query("DELETE FROM earnings WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM earnings")
    fun getAll(): LiveData<List<Earning>>
}