package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.entities.types.BudgetType

class BudgetTypesRepository (private val BudgetTypesDAO: BudgetTypesDAO) {
    val getAllSavings: LiveData<List<BudgetType>> = BudgetTypesDAO.getAll()

    suspend fun addBudgetType(item: BudgetType) {
        BudgetTypesDAO.insert(item)
    }

    suspend fun updateBudgetType(id: Int, newItem: BudgetType) {
        val previousItem = BudgetTypesDAO.getById(id)
        previousItem.title = newItem.title
        previousItem.sum = newItem.sum
        BudgetTypesDAO.update(previousItem)
    }

    suspend fun deleteBudgetType(id: Int) {
        BudgetTypesDAO.delete(id)
    }
}