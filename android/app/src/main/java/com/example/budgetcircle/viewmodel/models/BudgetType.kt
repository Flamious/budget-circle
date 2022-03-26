package com.example.budgetcircle.viewmodel.models

data class BudgetType(
    var id: Int,
    var title: String,
    var sum: Double,
    var isDeletable: Boolean
)