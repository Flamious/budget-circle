package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "expenses")
data class Expense (
    @PrimaryKey (autoGenerate = true) val id: Int,
    val sum: Float,
    val date: Date,
    val typeNumber: Int
)