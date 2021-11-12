package com.example.budgetcircle.database.entities.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_types")
data class BudgetType(
    var title: String,
    var sum: Double,
    var isDeletable: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
