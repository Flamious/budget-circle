package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "savings")
data class BudgetSaving (
    @PrimaryKey (autoGenerate = true) val id: Int,
    val sum: Float,
    val typeId: Int?
)
