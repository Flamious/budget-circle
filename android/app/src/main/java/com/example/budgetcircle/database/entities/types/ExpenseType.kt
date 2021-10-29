package com.example.budgetcircle.database.entities.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "expenses_types")
data class ExpenseType (
    @PrimaryKey (autoGenerate = true) val id: Int,
    val title: String
)