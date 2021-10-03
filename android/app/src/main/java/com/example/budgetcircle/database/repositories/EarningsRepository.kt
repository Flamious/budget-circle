package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.main.EarningsDAO
import com.example.budgetcircle.database.entities.main.Earning

class EarningsRepository(private val earningsDAO: EarningsDAO) {
    val getAllEarnings: LiveData<List<Earning>> = earningsDAO.getAll()

    suspend fun addEarning(item: Earning) {
        earningsDAO.insert(item)
    }

    suspend fun updateEarning(item: Earning) {
        earningsDAO.update(item)
    }

    suspend fun deleteEarning(id: Int) {
        earningsDAO.delete(id)
    }

}