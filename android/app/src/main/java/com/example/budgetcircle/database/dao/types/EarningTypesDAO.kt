package com.example.budgetcircle.database.dao.types

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.types.EarningType

@Dao
interface EarningTypesDAO {
    @Insert
    suspend fun insert(types: EarningType)

    @Update
    suspend fun update(types: EarningType)

    @Query("DELETE FROM earning_types WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM budget_types")
    fun getAll(): LiveData<List<EarningType>>
}