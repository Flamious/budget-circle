package com.example.budgetcircle.viewmodel.models

data class PlannedBudgetModel(
    val month: Int,
    val year: Int,
    val earnings: Double,
    val expenses: Double
)

data class PlannedBudget(
    val id: Int,
    val isPlanned: Boolean,
    val plannedEarnings: Double,
    val plannedExpenses: Double,
    val currentEarnings: Double,
    val currentExpenses: Double,
    val plannedBudget: Double,
    val currentBudget: Double,
    val earnings: List<OperationTypePart>,
    val expenses: List<OperationTypePart>,
    val accounts: List<OperationTypePart>,
)

data class OperationTypePart (
    val title: String,
    val sum: Double,
    val percentage: Double,
)