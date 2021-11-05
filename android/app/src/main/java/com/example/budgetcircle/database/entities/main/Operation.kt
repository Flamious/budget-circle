package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "operations")
data class Operation(
    val title: String,
    val sum: Double,
    val date: Date,
    val typeId: Int,
    val budgetTypeId: Int,
    val commentary: String,
    val wasRepetitive: Boolean,
    val isExpense: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}