package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "expenses")
data class Expense (
    val sum: Double,
    val date: Date,
    val typeId: Int,
    val budgetTypeId: Int
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int = 0
}