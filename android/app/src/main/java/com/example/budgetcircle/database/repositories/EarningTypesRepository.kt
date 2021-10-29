package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.types.EarningTypesDAO
import com.example.budgetcircle.database.entities.types.EarningType

class EarningTypesRepository (private val EarningTypesDAO: EarningTypesDAO) {
    val getAllEarningTypes: LiveData<List<EarningType>> = EarningTypesDAO.getAll()

    suspend fun addEarningType(item: EarningType) {
        EarningTypesDAO.insert(item)
    }

    suspend fun updateEarningType(item: EarningType) {
        EarningTypesDAO.update(item)
    }

    suspend fun deleteEarningType(id: Int) {
        EarningTypesDAO.delete(id)
    }
}