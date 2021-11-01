package com.example.budgetcircle.database.entities.main

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "earnings")
data class Earning (
    val sum: Float,
    val date: Date,
    val typeId: Int
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int = 0
}