package com.example.budgetcircle.database.entities.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses_types")
data class ExpenseType(
    val title: String,
    var titleRu: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}