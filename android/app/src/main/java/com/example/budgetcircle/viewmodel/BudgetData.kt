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
import com.example.budgetcircle.database.entities.main.OperationSum
import com.example.budgetcircle.database.entities.main.Expense
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.entities.types.EarningType
import com.example.budgetcircle.database.entities.types.ExpenseType
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
    private val expenseTypesRepository: ExpenseTypesRepository
    private val earningsRepository: EarningsRepository
    private val expensesRepository: ExpensesRepository
    val budgetTypes: LiveData<List<BudgetType>>
    val expenseSums: LiveData<List<OperationSum>>
    val earningTypes: List<EarningType>
    val expenseTypes: List<ExpenseType>

    var earningSumByDate: LiveData<List<OperationSum>>
    val earningsDate: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 7 //use week at start
    }

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

        earningSumByDate = Transformations.switchMap(earningsDate) { param ->
            if (param > 0) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -param)
                earningsDAO.getAllByDate(calendar.time)
            } else {
                earningsDAO.getAll()
            }
        }

        budgetTypes = budgetTypesDAO.getAll()
        earningTypes = earningTypesDAO.getAll()
        expenseTypes = expenseTypesDAO.getAll()
        expenseSums = expensesDAO.getAll()
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
        expensesSum.postValue(expensesRepository.getTotalSum())
    }

    fun addToBudgetTypesList(item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypesRepository.addBudgetType(item)
        totalSum.postValue(totalSum.value!! + item.sum)
    }

    fun editBudgetType(id: Int, item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypesRepository.updateBudgetType(id, item)
    }

    fun deleteBudgetType(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        earningsRepository.deleteByBudgetTypeId(id)
        expensesRepository.deleteByBudgetTypeId(id)
        budgetTypesRepository.deleteBudgetType(id)
        initSums()
    }

    fun addToOperationList(item: HistoryItem) {
        operations.value?.add(item)
    }

    fun addExpense(sum: Float, type: Int, budgetTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            totalSum.postValue(totalSum.value!! - sum)
            expensesSum.postValue(expensesSum.value!! + sum)
            budgetTypesRepository.addSum(budgetTypeId, -sum)
            expensesRepository.addExpense(Expense(sum, getCurrentDate(), type, budgetTypeId))
        }

    fun addEarning(sum: Float, type: Int, budgetTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            totalSum.postValue(totalSum.value!! + sum)
            earningsSum.postValue(earningsSum.value!! + sum)
            budgetTypesRepository.addSum(budgetTypeId, sum)
            earningsRepository.addEarning(Earning(sum, Date(), type, budgetTypeId))
        }

    private fun getCurrentDate(): Date {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }
}