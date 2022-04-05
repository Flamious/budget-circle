package com.example.budgetcircle.viewmodel.models

data class OperationType(
    val id: Int,
    val title: String,
    val sum: Double,
    val isDeletable: Boolean
)