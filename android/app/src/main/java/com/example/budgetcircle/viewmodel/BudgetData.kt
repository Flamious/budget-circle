package com.example.budgetcircle.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budgetcircle.database.DbBudget
import com.example.budgetcircle.database.dao.main.EarningsDAO
import com.example.budgetcircle.database.dao.main.ExpensesDAO
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.dao.types.EarningTypesDAO
import com.example.budgetcircle.database.dao.types.ExpenseTypesDAO
import com.example.budgetcircle.database.entities.main.Earning
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.entities.types.EarningType
import com.example.budgetcircle.database.repositories.*
import com.example.budgetcircle.forms.EarningsFormActivity
import com.example.budgetcircle.viewmodel.items.BudgetTypeAdapter
import com.example.budgetcircle.viewmodel.items.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

open class BudgetData(application: Application) : AndroidViewModel(application) {
    private val budgetTypesRepository: BudgetTypesRepository
    private val earningTypesRepository: EarningTypesRepository
    private val earningsRepository: EarningsRepository
    private val expensesRepository: ExpensesRepository
    val budgetTypes: LiveData<List<BudgetType>>
    val earningTypes: List<EarningType>

    init {
        val budgetTypesDAO: BudgetTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).BudgetTypesDAO()
        budgetTypesRepository = BudgetTypesRepository(budgetTypesDAO)

        val earningTypesDAO: EarningTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).EarningTypesDAO()
        earningTypesRepository = EarningTypesRepository(earningTypesDAO)

        val earningsDAO: EarningsDAO =
            DbBudget.getDatabase(application, viewModelScope).EarningsDAO()
        earningsRepository = EarningsRepository(earningsDAO)

        val expensesDAO: ExpensesDAO =
            DbBudget.getDatabase(application, viewModelScope).ExpensesDAO()
        expensesRepository = ExpensesRepository(expensesDAO)

        budgetTypes = budgetTypesDAO.getAll()
        earningTypes = earningTypesDAO.getAll()
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

    fun initSums() = viewModelScope.launch(Dispatchers.IO) {
        totalSum.postValue(budgetTypesRepository.getTotalSum())
        earningsSum.postValue(earningsRepository.getTotalSum())
    }
    fun updateSum() = viewModelScope.launch(Dispatchers.IO) {
        totalSum.postValue(budgetTypesRepository.getTotalSum())
    }
    fun addToBudgetTypesList(item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypesRepository.addBudgetType(item)
        updateSum()
    }

    fun editBudgetType(id: Int, item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypesRepository.updateBudgetType(id, item)
        updateSum()
    }

    fun deleteBudgetType(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        earningsRepository.deleteByBudgetTypeId(id)
        budgetTypesRepository.deleteBudgetType(id)
        updateSum()
    }

    fun addToOperationList(item: HistoryItem) {
        operations.value?.add(item)
    }

    fun addExpense(sum: Float?) {
        totalSum.value?.let { tSum ->
            totalSum.value = tSum - sum!!
        }
        expensesSum.value?.let { expSum ->
            expensesSum.value = expSum + sum!!
        }
    }

    fun addEarning(sum: Float, type: Int, budgetTypeId: Int) = viewModelScope.launch (Dispatchers.IO) {
        totalSum.postValue(totalSum.value!! + sum)
        earningsSum.postValue(earningsSum.value!! + sum)
        budgetTypesRepository.addSum(budgetTypeId, sum)
        earningsRepository.addEarning(Earning(sum, Date(), type, budgetTypeId))
    }
}