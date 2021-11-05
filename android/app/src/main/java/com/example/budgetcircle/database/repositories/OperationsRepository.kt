package com.example.budgetcircle.database.repositories

import com.example.budgetcircle.database.dao.main.OperationsDAO
import com.example.budgetcircle.database.entities.main.Operation
import com.example.budgetcircle.settings.DoubleFormatter

class OperationsRepository(private val operationsDAO: OperationsDAO) {
    suspend fun getTotalExpensesSum(): Double {
        var sum = 0.0
        for (i in operationsDAO.getExpensesSums()) {
            sum += i
        }
        return DoubleFormatter.format(sum)
    }

    suspend fun getTotalEarningsSum(): Double {
        var sum = 0.0
        for (i in operationsDAO.getExpensesSums()) {
            sum += i
        }
        return DoubleFormatter.format(sum)
    }

    suspend fun addOperation(item: Operation) {
        operationsDAO.insert(item)
    }

    suspend fun updateOperation(item: Operation) {
        operationsDAO.update(item)
    }

    suspend fun deleteByBudgetTypeId(id: Int) {
        operationsDAO.deleteByBudgetTypeId(id)
    }

    suspend fun deleteOperation(id: Int) {
        operationsDAO.delete(id)
    }
}