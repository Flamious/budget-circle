package com.example.budgetcircle.database.entities.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "budget_types")
data class BudgetType (
    @PrimaryKey (autoGenerate = true) val id: Int,
    val title: String,
    val sum: Float,
    val isDeletable: Boolean
)
