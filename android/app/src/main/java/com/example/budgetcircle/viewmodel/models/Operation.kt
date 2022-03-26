package com.example.budgetcircle.viewmodel.models

data class Operation(
    var id: Int,
    var title: String,
    var sum: Double,
    var date: String,
    var typeId: Int,
    var budgetTypeId: Int,
    var commentary: String,
    var isExpense: Boolean?
)

data class OperationSum(val sum: Double, val type: String )