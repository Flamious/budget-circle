package com.example.budgetcircle.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations")
data class Operation(
    var title: String,
    var sum: Double,
    var typeId: Int,
    var budgetTypeId: Int,
    var commentary: String,
    var isExpense: Boolean?,
    var user: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}