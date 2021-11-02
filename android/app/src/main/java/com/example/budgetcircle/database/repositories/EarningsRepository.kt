package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.main.EarningsDAO
import com.example.budgetcircle.database.entities.main.Earning

class EarningsRepository(private val earningsDAO: EarningsDAO) {
    suspend fun getTotalSum(): Float {
        var sum = 0f
        for (i in earningsDAO.getSums()) {
            sum += i
        }
        return sum
    }

    suspend fun addEarning(item: Earning) {
        earningsDAO.insert(item)
    }

    suspend fun updateEarning(item: Earning) {
        earningsDAO.update(item)
    }

    suspend fun deleteByBudgetTypeId(id: Int) {
        earningsDAO.deleteByBudgetTypeId(id)
    }

    suspend fun deleteEarning(id: Int) {
        earningsDAO.delete(id)
    }

}