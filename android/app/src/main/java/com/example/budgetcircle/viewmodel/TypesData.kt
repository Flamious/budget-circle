package com.example.budgetcircle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetcircle.database.DbBudget
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.dao.types.EarningTypesDAO
import com.example.budgetcircle.database.dao.types.ExpenseTypesDAO
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.entities.types.EarningType
import com.example.budgetcircle.database.entities.types.ExpenseType
import com.example.budgetcircle.database.repositories.BudgetTypesRepository
import com.example.budgetcircle.database.repositories.EarningTypesRepository
import com.example.budgetcircle.database.repositories.ExpenseTypesRepository

class TypesData(application: Application) : AndroidViewModel(application) {
    /*private val budgetTypesRepository: BudgetTypesRepository
    private val earningTypesRepository: EarningTypesRepository
    private val expenseTypesRepository: ExpenseTypesRepository
    val budgetTypes: LiveData<List<BudgetType>>
    val earningsTypes: LiveData<List<EarningType>>
    val expensesTypes: LiveData<List<ExpenseType>>

    init {
        val budgetTypesDAO: BudgetTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).BudgetTypesDAO()
        budgetTypesRepository = BudgetTypesRepository(budgetTypesDAO)

        val earningTypesDAO: EarningTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).EarningTypesDAO()
        earningTypesRepository = EarningTypesRepository(earningTypesDAO)

        val expenseTypesDAO: ExpenseTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).ExpenseTypesDAO()
        expenseTypesRepository = ExpenseTypesRepository(expenseTypesDAO)

        budgetTypes = budgetTypesDAO.getAll()
        earningsTypes = earningTypesDAO.getAll()
        expensesTypes = expenseTypesDAO.getAll()
    }*/


}