package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.main.ExpensesDAO
import com.example.budgetcircle.database.entities.main.Expense

class ExpensesRepository(private val expensesDAO: ExpensesDAO) {
    val getAllExpenses: LiveData<List<Expense>> = expensesDAO.getAll()

    suspend fun addExpense(item: Expense) {
        expensesDAO.insert(item)
    }

    suspend fun updateExpense(item: Expense) {
        expensesDAO.update(item)
    }

    suspend fun deleteExpense(id: Int) {
        expensesDAO.delete(id)
    }

}