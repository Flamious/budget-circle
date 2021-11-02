package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.main.ExpensesDAO
import com.example.budgetcircle.database.entities.main.Expense

class ExpensesRepository(private val expensesDAO: ExpensesDAO) {
    suspend fun getTotalSum(): Float {
        var sum = 0f
        for (i in expensesDAO.getSums()) {
            sum += i
        }
        return sum
    }

    suspend fun addExpense(item: Expense) {
        expensesDAO.insert(item)
    }

    suspend fun updateExpense(item: Expense) {
        expensesDAO.update(item)
    }

    suspend fun deleteByBudgetTypeId(id: Int) {
        expensesDAO.deleteByBudgetTypeId(id)
    }

    suspend fun deleteExpense(id: Int) {
        expensesDAO.delete(id)
    }

}