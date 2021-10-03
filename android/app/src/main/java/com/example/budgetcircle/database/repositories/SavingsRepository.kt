package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.main.SavingsDAO
import com.example.budgetcircle.database.entities.main.BudgetSaving

class SavingsRepository(private val savingsDAO: SavingsDAO) {
    val getAllSavings: LiveData<List<BudgetSaving>> = savingsDAO.getAll()

    suspend fun addSaving(item: BudgetSaving) {
        savingsDAO.insert(item)
    }

    suspend fun updateSaving(item: BudgetSaving) {
        savingsDAO.update(item)
    }

    suspend fun deleteSaving(id: Int) {
        savingsDAO.delete(id)
    }

}