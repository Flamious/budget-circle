package com.example.budgetcircle.database.repositories

import com.example.budgetcircle.database.dao.BudgetTypeDao
import com.example.budgetcircle.database.entities.BudgetType

class BudgetTypeRepository(private val budgetTypeDao: BudgetTypeDao) {
    fun addBudgetTypes(types: List<BudgetType>) {
        budgetTypeDao.insertMany(types)
    }

    fun deleteBudgetType(id: Int) {
        budgetTypeDao.delete(id)
    }

    fun deleteAll() = budgetTypeDao.deleteAll()
}