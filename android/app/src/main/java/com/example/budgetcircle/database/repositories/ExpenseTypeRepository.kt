package com.example.budgetcircle.database.repositories

import com.example.budgetcircle.database.dao.ExpenseTypeDao
import com.example.budgetcircle.database.entities.ExpenseType

class ExpenseTypeRepository(private val expenseTypeDao: ExpenseTypeDao) {
    fun addExpenseType(types: List<ExpenseType>) {
        expenseTypeDao.insertMany(types)
    }

    fun deleteExpenseType(id: Int) {
        expenseTypeDao.delete(id)
    }

    fun deleteAll() = expenseTypeDao.deleteAll()
}