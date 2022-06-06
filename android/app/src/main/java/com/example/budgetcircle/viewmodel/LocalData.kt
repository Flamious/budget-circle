package com.example.budgetcircle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetcircle.database.DbBudget
import com.example.budgetcircle.database.dao.BudgetTypeDao
import com.example.budgetcircle.database.dao.EarningTypeDao
import com.example.budgetcircle.database.dao.ExpenseTypeDao
import com.example.budgetcircle.database.dao.OperationDao
import com.example.budgetcircle.database.entities.BudgetType
import com.example.budgetcircle.database.entities.EarningType
import com.example.budgetcircle.database.entities.ExpenseType
import com.example.budgetcircle.database.entities.Operation
import com.example.budgetcircle.database.repositories.BudgetTypeRepository
import com.example.budgetcircle.database.repositories.EarningTypeRepository
import com.example.budgetcircle.database.repositories.ExpenseTypeRepository
import com.example.budgetcircle.database.repositories.OperationRepository
import com.example.budgetcircle.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalData(application: Application) : AndroidViewModel(application) {
    private val budgetTypeRepository: BudgetTypeRepository
    private val operationRepository: OperationRepository
    private val earningTypeRepository: EarningTypeRepository
    private val expenseTypeRepository: ExpenseTypeRepository

    val budgetTypes: LiveData<List<BudgetType>>
    val operations: LiveData<List<Operation>>
    val earningTypes: LiveData<List<EarningType>>
    val expenseTypes: LiveData<List<ExpenseType>>

    init {
        val budgetTypeDAO: BudgetTypeDao =
            DbBudget.getDatabase(application, viewModelScope).BudgetTypesDAO()
        budgetTypeRepository = BudgetTypeRepository(budgetTypeDAO)

        val operationDAO: OperationDao =
            DbBudget.getDatabase(application, viewModelScope).OperationsDAO()
        operationRepository = OperationRepository(operationDAO)

        val earningTypeDAO: EarningTypeDao =
            DbBudget.getDatabase(application, viewModelScope).EarningTypesDAO()
        earningTypeRepository = EarningTypeRepository(earningTypeDAO)

        val expenseTypeDAO: ExpenseTypeDao =
            DbBudget.getDatabase(application, viewModelScope).ExpenseTypesDAO()
        expenseTypeRepository = ExpenseTypeRepository(expenseTypeDAO)

        budgetTypes = budgetTypeDAO.getAll()
        earningTypes = earningTypeDAO.getAll()
        expenseTypes = expenseTypeDAO.getAll()
        operations = operationDAO.getAll(Settings.login)
    }

    fun postBudgetTypes(types: List<BudgetType>) {
        budgetTypeRepository.deleteAll()
        budgetTypeRepository.addBudgetTypes(types)
    }

    fun postEarningTypes(types: List<EarningType>) {
        earningTypeRepository.deleteAll()
        earningTypeRepository.addEarningTypes(types)
    }

    fun postExpenseTypes(types: List<ExpenseType>) {
        expenseTypeRepository.deleteAll()
        expenseTypeRepository.addExpenseType(types)
    }

    fun addOperation(operation: Operation) = viewModelScope.launch(Dispatchers.IO) {
        operationRepository.addOperation(operation)
    }

    fun editOperation(id: Int, operation: Operation) = viewModelScope.launch(Dispatchers.IO) {
        operationRepository.updateOperation(id, operation)
    }

    fun deleteOperation(operationId: Int) = viewModelScope.launch(Dispatchers.IO) {
        operationRepository.deleteOperation(operationId)
    }
}
