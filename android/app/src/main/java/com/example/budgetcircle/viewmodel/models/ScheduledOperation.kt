package com.example.budgetcircle.viewmodel.models

data class ScheduledOperation(
    var id: Int,
    var title: String,
    var sum: Double,
    var typeId: Int,
    var budgetTypeId: Int,
    var commentary: String,
    var isExpense: Boolean
)