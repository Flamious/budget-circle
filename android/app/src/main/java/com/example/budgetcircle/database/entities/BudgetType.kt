package com.example.budgetcircle.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_types")
data class BudgetType(
    @PrimaryKey var id: Int,
    var title: String,
    var sum: Double
)