package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.entities.types.BudgetType

class BudgetTypesRepository (private val BudgetTypesDAO: BudgetTypesDAO) {
    val getAllSavings: LiveData<List<BudgetType>> = BudgetTypesDAO.getAll()

    suspend fun addBudgetType(item: BudgetType) {
        BudgetTypesDAO.insert(item)
    }

    suspend fun updateSaving(item: BudgetType) {
        BudgetTypesDAO.update(item)
    }

    suspend fun deleteSaving(id: Int) {
        BudgetTypesDAO.delete(id)
    }
}