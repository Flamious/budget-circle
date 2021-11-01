package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.types.ExpenseTypesDAO
import com.example.budgetcircle.database.entities.types.ExpenseType

class ExpenseTypesRepository(private val ExpenseTypesDAO: ExpenseTypesDAO) {
    suspend fun getAllEarningTypes(): List<ExpenseType> {
        return ExpenseTypesDAO.getAll()
    }

    suspend fun addEarningType(item: ExpenseType) {
        ExpenseTypesDAO.insert(item)
    }

    suspend fun updateEarningType(item: ExpenseType) {
        ExpenseTypesDAO.update(item)
    }

    suspend fun deleteEarningType(id: Int) {
        ExpenseTypesDAO.delete(id)
    }
}