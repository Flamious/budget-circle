package com.example.budgetcircle.database.dao.types

import androidx.room.*
import com.example.budgetcircle.database.entities.types.SavingType

@Dao
interface SavingTypesDAO {
    @Insert
    suspend fun insert(types: SavingType)

    @Update
    suspend fun update(types: SavingType)
    
    @Delete
    suspend fun delete(types: SavingType)

    @Query("SELECT * FROM saving_types")
    suspend fun getAll(): List<SavingType>
}