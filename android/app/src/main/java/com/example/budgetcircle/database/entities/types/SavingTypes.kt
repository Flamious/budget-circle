package com.example.budgetcircle.database.entities.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "saving_types")
data class SavingType (
    @PrimaryKey (autoGenerate = true) val id: Int,
    val name: String,
    val isCustom: Boolean,
    val color: String
)
