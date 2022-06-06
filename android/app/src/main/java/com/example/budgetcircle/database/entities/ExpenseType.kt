package com.example.budgetcircle.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_types")
class ExpenseType(
    @PrimaryKey var id: Int,
    var title: String,
    var sum: Double
)