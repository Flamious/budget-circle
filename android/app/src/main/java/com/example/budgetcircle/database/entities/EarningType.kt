package com.example.budgetcircle.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earning_types")
class EarningType(
    @PrimaryKey var id: Int,
    var title: String,
    var sum: Double
)