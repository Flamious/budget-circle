package com.example.budgetcircle.database.dao.types

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetcircle.database.entities.types.EarningType

@Dao
interface EarningTypesDAO {
    @Insert
    fun insert(vararg types: EarningType)

    @Update
    fun update(types: EarningType)

    @Query("DELETE FROM earning_types WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM earning_types")
    fun deleteAll()


    @Query("SELECT * FROM earning_types")
    fun getAll(): List<EarningType>
}