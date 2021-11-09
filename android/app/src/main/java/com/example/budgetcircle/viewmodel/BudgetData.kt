package com.example.budgetcircle.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budgetcircle.R
import com.example.budgetcircle.database.DbBudget
import com.example.budgetcircle.database.dao.main.OperationsDAO
import com.example.budgetcircle.database.dao.types.BudgetTypesDAO
import com.example.budgetcircle.database.dao.types.EarningTypesDAO
import com.example.budgetcircle.database.dao.types.ExpenseTypesDAO
import com.example.budgetcircle.database.entities.main.OperationSum
import com.example.budgetcircle.database.entities.main.Operation
import com.example.budgetcircle.database.entities.types.BudgetType
import com.example.budgetcircle.database.entities.types.EarningType
import com.example.budgetcircle.database.entities.types.ExpenseType
import com.example.budgetcircle.database.repositories.*
import com.example.budgetcircle.viewmodel.items.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

open class BudgetData(application: Application) : AndroidViewModel(application) {
    private val budgetTypesRepository: BudgetTypesRepository
    private val earningTypesRepository: EarningTypesRepository
    private val expenseTypesRepository: ExpenseTypesRepository
    private val operationsRepository: OperationsRepository
    val budgetTypes: LiveData<List<BudgetType>>
    val historyItems: LiveData<List<Operation>>
    val earningTypes: List<EarningType>
    val expenseTypes: List<ExpenseType>

    var earningSumByDate: LiveData<List<OperationSum>>
    var expenseSumByDate: LiveData<List<OperationSum>>
    val earningsDate: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 7 //use week at start
    }
    val expensesDate: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 7 //use week at start
    }
    val earningsDateString: MutableLiveData<String> = MutableLiveData<String>()
    val expensesDateString: MutableLiveData<String> = MutableLiveData<String>()
    val chosenHistoryItem: MutableLiveData<HistoryItem?> = MutableLiveData<HistoryItem?>().apply {
        value = null
    }
    val chosenHistoryItemIndex: MutableLiveData<Int?> = MutableLiveData<Int?>().apply {
        value = null
    }

    init {
        val budgetTypesDAO: BudgetTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).BudgetTypesDAO()
        budgetTypesRepository = BudgetTypesRepository(budgetTypesDAO)

        val operationsDAO: OperationsDAO =
            DbBudget.getDatabase(application, viewModelScope).OperationsDAO()
        operationsRepository = OperationsRepository(operationsDAO)

        val earningTypesDAO: EarningTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).EarningTypesDAO()
        earningTypesRepository = EarningTypesRepository(earningTypesDAO)

        val expenseTypesDAO: ExpenseTypesDAO =
            DbBudget.getDatabase(application, viewModelScope).ExpenseTypesDAO()
        expenseTypesRepository = ExpenseTypesRepository(expenseTypesDAO)

        earningSumByDate = Transformations.switchMap(earningsDate) { param ->
            if (param > 0) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -param)
                operationsDAO.getAllEarningsByDate(calendar.time)
            } else {
                operationsDAO.getAllEarnings()
            }
        }

        expenseSumByDate = Transformations.switchMap(expensesDate) { param ->
            if (param > 0) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -param)
                operationsDAO.getAllExpensesByDate(calendar.time)
            } else {
                operationsDAO.getAllExpenses()
            }
        }

        budgetTypes = budgetTypesDAO.getAll()
        earningTypes = earningTypesDAO.getAll()
        expenseTypes = expenseTypesDAO.getAll()
        historyItems = operationsDAO.getAllHistoryItems()
    }

    fun addToBudgetTypesList(item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        val id = budgetTypesRepository.addBudgetType(item)
        if (item.sum > 0.0) {
            operationsRepository.addOperation(
                Operation(
                    "${getApplication<Application>().resources.getString(R.string.new_account)}: ${item.title}",
                    item.sum,
                    getCurrentDate(),
                    earningTypes[earningTypes.lastIndex].id,
                    id,
                    "",
                    isRepetitive = false,
                    isExpense = false
                )
            )
        }
    }

    fun editBudgetType(id: Int, item: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypesRepository.updateBudgetType(id, item)
    }

    fun deleteBudgetType(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        operationsRepository.deleteByBudgetTypeId(id)
        budgetTypesRepository.deleteBudgetType(id)
    }

    fun makeExchange(idFrom: Int, idTo: Int, sum: Double) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypesRepository.addSum(idFrom, -sum)
        budgetTypesRepository.addSum(idTo, sum)
    }

    fun addExpense(title: String, sum: Double, type: Int, budgetTypeId: Int, commentary: String) =
        viewModelScope.launch(Dispatchers.IO) {
            budgetTypesRepository.addSum(budgetTypeId, -sum)
            operationsRepository.addOperation(
                Operation(
                    title,
                    sum,
                    getCurrentDate(),
                    type,
                    budgetTypeId,
                    commentary,
                    isRepetitive = false,
                    isExpense = true
                )
            )
        }

    fun addEarning(title: String, sum: Double, type: Int, budgetTypeId: Int, commentary: String) =
        viewModelScope.launch(Dispatchers.IO) {
            budgetTypesRepository.addSum(budgetTypeId, sum)
            operationsRepository.addOperation(
                Operation(
                    title,
                    sum,
                    getCurrentDate(),
                    type,
                    budgetTypeId,
                    commentary,
                    isRepetitive = false,
                    isExpense = false
                )
            )
        }

    fun deleteOperation(operation: HistoryItem): Boolean {
        val budgetType: BudgetType = budgetTypes.value!!.first { it.title == operation.budgetType }
        if (!operation.isExpense) {
            if (budgetType.sum < operation.sum) return false
            viewModelScope.launch(Dispatchers.IO) {
                budgetTypesRepository.addSum(budgetType.id, -operation.sum)
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                budgetTypesRepository.addSum(budgetType.id, operation.sum)
            }

        }
        viewModelScope.launch(Dispatchers.IO) {
            operationsRepository.deleteOperation(operation.id)
        }
        return true
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