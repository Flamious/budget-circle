package com.example.budgetcircle.database.repositories

import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.settings.DoubleFormatter

class BudgetTypesRepository(private val BudgetTypesDAO: BudgetTypesDAO) {
    fun addBudgetType(item: BudgetType): Int {
        BudgetTypesDAO.insert(item)
        return BudgetTypesDAO.getLastItem().id
    }

    fun updateBudgetType(id: Int, newItem: BudgetType) {
        val previousItem = BudgetTypesDAO.getById(id)
        previousItem.title = newItem.title
        previousItem.sum = DoubleFormatter.format(newItem.sum)
        BudgetTypesDAO.update(previousItem)
    }

    fun addSum(id: Int, sum: Double) {
        val item = BudgetTypesDAO.getById(id)
        item.sum = DoubleFormatter.format(item.sum + sum)
        BudgetTypesDAO.update(item)
    }

    fun deleteBudgetType(id: Int) {
        BudgetTypesDAO.delete(id)
    }
}