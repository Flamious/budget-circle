package com.example.budgetcircle.database.repositories

import androidx.lifecycle.LiveData
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.entities.types.BudgetType

class BudgetTypesRepository (private val BudgetTypesDAO: BudgetTypesDAO) {
    val getAllBudgetTypes: LiveData<List<BudgetType>> = BudgetTypesDAO.getAll()

    suspend fun getTotalSum(): Float {
        var sum = 0f
        for (i in BudgetTypesDAO.getSums()) {
            sum += i
        }
        return sum
    }

    suspend fun addBudgetType(item: BudgetType) {
        BudgetTypesDAO.insert(item)
    }

    suspend fun updateBudgetType(id: Int, newItem: BudgetType) {
        val previousItem = BudgetTypesDAO.getById(id)
        previousItem.title = newItem.title
        previousItem.sum = newItem.sum
        BudgetTypesDAO.update(previousItem)
    }

    suspend fun addSum(id: Int, sum: Float) {
        val item = BudgetTypesDAO.getById(id)
        item.sum += sum
        BudgetTypesDAO.update(item)
    }

    suspend fun deleteBudgetType(id: Int) {
        BudgetTypesDAO.delete(id)
    }
}