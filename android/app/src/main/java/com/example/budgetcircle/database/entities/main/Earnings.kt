package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "earnings")
data class Earning (
    @PrimaryKey (autoGenerate = true) val id: Int,
    val sum: Float,
    val date: Date,
    val typeNumber: Int
)