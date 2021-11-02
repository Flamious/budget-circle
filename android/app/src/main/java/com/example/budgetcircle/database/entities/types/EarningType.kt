package com.example.budgetcircle.database.entities.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earning_types")
data class EarningType(
    val title: String
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int = 0
}