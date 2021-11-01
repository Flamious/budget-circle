package com.example.budgetcircle.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budgetcircle.database.DbBudget
import com.example.budgetcircle.database.dao.main.EarningsDAO
import com.example.budgetcircle.database.dao.main.ExpensesDAO
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.dao.types.EarningTypesDAO
import com.example.budgetcircle.database.dao.types.ExpenseTypesDAO
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.repositories.*
import com.example.budgetcircle.forms.EarningsFormActivity
/*import com.example.budgetcircle.viewmodel.items.BudgetType*/
import com.example.budgetcircle.viewmodel.items.BudgetTypeAdapter
import com.example.budgetcircle.viewmodel.items.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BudgetData(application: Application) : AndroidViewModel(application) {
    private val budgetTypesRepository: BudgetTypesRepository
    private val earningTypesRepository: EarningTypesRepository
    private val expenseTypesRepository: ExpenseTypesRepository
    private val earningsRepository: EarningsRepository
    private val expensesRepository: ExpensesRepository
    val budgetTypes: LiveData<List<BudgetType>>

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

        val earningsDAO: EarningsDAO =
            DbBudget.getDatabase(application, viewModelScope).EarningsDAO()
        earningsRepository = EarningsRepository(earningsDAO)

        val expensesDAO: ExpensesDAO =
            DbBudget.getDatabase(application, viewModelScope).ExpensesDAO()
        expensesRepository = ExpensesRepository(expensesDAO)

        budgetTypes = budgetTypesDAO.getAll()
    }

    val totalSum: MutableLiveData<Float> = MutableLiveData<Float>().apply {
        value = 0f
    }

    val expensesSum: MutableLiveData<Float> = MutableLiveData<Float>().apply {
        value = 0f
    }

    val earningsSum: MutableLiveData<Float> = MutableLiveData<Float>().apply {
        value = 0f
    }

    val operations: MutableLiveData<MutableList<HistoryItem>> =
        MutableLiveData<MutableList<HistoryItem>>().apply {
            value = mutableListOf()
        }

    /*val budgetTypes: MutableLiveData<MutableList<BudgetType>> =
        MutableLiveData<MutableList<BudgetType>>().apply {
            value = mutableListOf()
            value?.add(BudgetType(0, 0f, "Cash0", false))
            value?.add(BudgetType(1, 0f, "Cash1", false))
            value?.add(BudgetType(2, 0f, "Cash2", false))
            value?.add(BudgetType(3, 0f, "Cash3", true))
        }*/

    fun editBudgetType(id: Int, item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        /*budgetTypes.value?.let { value ->
            val index = value.indexOfFirst { it.id == item.id }
            value[index] = item.copy()
            budgetTypes.postValue(budgetTypes.value)

        }*/
        budgetTypesRepository.updateBudgetType(id, item)
    }

    fun deleteBudgetType(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        /*budgetTypes.value?.let { value ->
            val index = value.indexOfFirst { it.id == id }
            budgetTypes.value?.removeAt(index)
            budgetTypes.postValue(budgetTypes.value)
        }*/
        budgetTypesRepository.deleteBudgetType(id)
    }

    fun addToOperationList(item: HistoryItem) {
        operations.value?.add(item)
    }

    fun addToBudgetTypesList(item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        /*budgetTypes.value?.add(type)*/
        budgetTypesRepository.addBudgetType(item)
    }

    fun addExpense(sum: Float?) {
        totalSum.value?.let { tSum ->
            totalSum.value = tSum - sum!!
        }
        expensesSum.value?.let { expSum ->
            expensesSum.value = expSum + sum!!
        }
    }

    fun addEarning(sum: Float?) {
        totalSum.value?.let { tSum ->
            totalSum.value = tSum + sum!!
        }
        earningsSum.value?.let { earnSum ->
            earningsSum.value = earnSum + sum!!
        }
    }
}