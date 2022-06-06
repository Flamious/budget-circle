package com.example.budgetcircle.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.budgetcircle.database.entities.Operation

@Dao
interface OperationDao {
    @Query("SELECT * FROM operations WHERE id = :id")
    fun getById(id: Int): Operation

    @Query("SELECT * FROM operations WHERE user = :login")
    fun getAll(login: String): LiveData<List<Operation>>

    @Insert
    suspend fun insert(item: Operation)

    @Update
    suspend fun update(item: Operation)

    @Query("DELETE FROM operations WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM operations WHERE user = :login")
    suspend fun deleteByLogin(login: String)
}