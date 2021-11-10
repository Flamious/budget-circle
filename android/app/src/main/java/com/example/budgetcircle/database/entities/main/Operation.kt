package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "operations")
data class Operation(
    var title: String,
    var sum: Double,
    var date: Date,
    var typeId: Int,
    var budgetTypeId: Int,
    var commentary: String,
    var isRepetitive: Boolean,
    var isExpense: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}