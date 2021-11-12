package com.example.budgetcircle.database.repositories

import com.example.budgetcircle.database.dao.main.OperationsDAO
import com.example.budgetcircle.database.entities.main.Operation
import com.example.budgetcircle.settings.DoubleFormatter

class OperationsRepository(private val operationsDAO: OperationsDAO) {
    suspend fun addOperation(item: Operation) {
        operationsDAO.insert(item)
    }

    suspend fun updateOperation(id: Int, newItem: Operation) {
        val previousItem = operationsDAO.getById(id)
        previousItem.sum = DoubleFormatter.format(newItem.sum)
        previousItem.title = newItem.title
        previousItem.typeId = newItem.typeId
        previousItem.budgetTypeId = newItem.budgetTypeId
        previousItem.commentary = newItem.commentary
        operationsDAO.update(previousItem)
    }

    fun deleteByBudgetTypeId(id: Int) {
        operationsDAO.deleteByBudgetTypeId(id)
    }

    suspend fun deleteOperation(id: Int) {
        operationsDAO.delete(id)
    }
}