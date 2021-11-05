package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "operations")
data class Operation(
    val sum: Double,
    val date: Date,
    val typeId: Int,
    val budgetTypeId: Int,
    val isExpense: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}