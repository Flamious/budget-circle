package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.budgetcircle.database.entities.types.BudgetType
import java.util.*

@Entity (tableName = "earnings")
data class Earning (
    val sum: Double,
    val date: Date,
    val typeId: Int,
    val budgetTypeId: Int
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int = 0
}