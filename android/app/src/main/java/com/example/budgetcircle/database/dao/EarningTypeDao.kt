package com.example.budgetcircle.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.budgetcircle.database.entities.EarningType

@Dao
interface EarningTypeDao {
    @Query("SELECT * FROM earning_types")
    fun getAll(): LiveData<List<EarningType>>

    @Insert
    fun insertMany(types: List<EarningType>)

    @Query("DELETE FROM earning_types WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM earning_types")
    fun deleteAll()
}