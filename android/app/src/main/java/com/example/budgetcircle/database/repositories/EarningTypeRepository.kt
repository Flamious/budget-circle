package com.example.budgetcircle.database.repositories

import com.example.budgetcircle.database.dao.EarningTypeDao
import com.example.budgetcircle.database.entities.EarningType

class EarningTypeRepository(private val earningTypeDao: EarningTypeDao) {
    fun addEarningTypes(types: List<EarningType>) {
        earningTypeDao.insertMany(types)
    }

    fun deleteEarningType(id: Int) {
        earningTypeDao.delete(id)
    }

    fun deleteAll() = earningTypeDao.deleteAll()
}