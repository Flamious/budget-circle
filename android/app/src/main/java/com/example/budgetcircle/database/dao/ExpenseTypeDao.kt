package com.example.budgetcircle.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.budgetcircle.database.entities.ExpenseType

@Dao
interface ExpenseTypeDao {
    @Query("SELECT * FROM expense_types")
    fun getAll(): LiveData<List<ExpenseType>>

    @Insert
    fun insertMany(types: List<ExpenseType>)

    @Query("DELETE FROM expense_types WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM expense_types")
    fun deleteAll()
}