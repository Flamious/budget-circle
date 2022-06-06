package com.example.budgetcircle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetcircle.R
import com.example.budgetcircle.requests.*
import com.example.budgetcircle.requests.models.ManyOperationsBody
import com.example.budgetcircle.requests.models.OperationListResponse
import com.example.budgetcircle.settings.Settings
import com.example.budgetcircle.viewmodel.items.HistoryItem
import com.example.budgetcircle.viewmodel.models.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BudgetCircleData(application: Application) : AndroidViewModel(application) {
    private val budgetTypeApiService: BudgetTypeApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(BudgetTypeApi::class.java)
    private val operationApiService: OperationApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(OperationApi::class.java)
    private val operationTypeApiService: OperationTypeApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(OperationTypeApi::class.java)
    private val scheduledOperationApiService: ScheduledOperationApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(ScheduledOperationApi::class.java)
    private val plannedBudgetApiService: PlannedBudgetApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(PlannedBudgetApi::class.java)

    val plannedBudget: MutableLiveData<PlannedBudget> = MutableLiveData<PlannedBudget>().apply {
        value = null
    }
    val chosenHistoryItem: MutableLiveData<HistoryItem?> = MutableLiveData<HistoryItem?>().apply {
        value = null
    }
    val chosenHistoryItemIndex: MutableLiveData<Int?> = MutableLiveData<Int?>().apply {
        value = null
    }
    var operations: MutableLiveData<List<Operation>> = MutableLiveData<List<Operation>>().apply {
        value = null
    }
    var budgetTypes: MutableLiveData<List<BudgetType>> = MutableLiveData<List<BudgetType>>().apply {
        value = null
    }
    var earningTypes: MutableLiveData<List<OperationType>> =
        MutableLiveData<List<OperationType>>().apply {
            value = null
        }
    var expenseTypes: MutableLiveData<List<OperationType>> =
        MutableLiveData<List<OperationType>>().apply {
            value = null
        }
    val earningsDate: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 7 //use week at start
    }
    val earningsDateString: MutableLiveData<String> = MutableLiveData<String>()
    val expensesDate: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 7 //use week at start
    }
    val expensesDateString: MutableLiveData<String> = MutableLiveData<String>()
    var earningSums: MutableLiveData<List<OperationSum>> =
        MutableLiveData<List<OperationSum>>().apply {
            value = null
        }
    var expenseSums: MutableLiveData<List<OperationSum>> =
        MutableLiveData<List<OperationSum>>().apply {
            value = null
        }
    val page: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 1
    }
    val isLastPage: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }
    val operationListDate: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 7 //use week at start
    }
    val operationListDateString: MutableLiveData<String> = MutableLiveData<String>()
    val operationType: MutableLiveData<String> = MutableLiveData<String>()
    val operationListChosenBudgetType: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 0
    }
    val operationListChosenBudgetTypeString: MutableLiveData<String> = MutableLiveData<String>()
    val operationListChosenType: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 0
    }
    val operationChartChosenBudgetTypeString: MutableLiveData<String> = MutableLiveData<String>()
    val operationChartChosenBudgetType: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 0
    }
    val operationListChosenTypeString: MutableLiveData<String> = MutableLiveData<String>()
    val operationListStartWith: MutableLiveData<String> = MutableLiveData<String>()
    val chartOperationPeriod: MutableLiveData<String> = MutableLiveData<String>()
    var chartOperations: MutableLiveData<List<ChartOperation>> =
        MutableLiveData<List<ChartOperation>>().apply {
            value = null
        }
    var scheduledOperationList: MutableLiveData<List<ScheduledOperation>> =
        MutableLiveData<List<ScheduledOperation>>().apply {
            value = null
        }

    init {
        getBudgetTypes()
        getEarningTypes()
        getExpenseTypes()
    }

    private fun getBudgetTypes() = viewModelScope.launch(Dispatchers.IO) {
        budgetTypeApiService.getBudgetTypes(
            Settings.token
        ).enqueue(object : Callback<List<BudgetType>> {
            override fun onFailure(call: Call<List<BudgetType>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<List<BudgetType>>,
                response: Response<List<BudgetType>>
            ) {
                if (response.isSuccessful) {
                    budgetTypes.value = response.body()!!.toList()
                }
            }
        })
    }

    fun addBudgetType(type: BudgetType) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypeApiService.addBudgetType(
            Settings.token,
            type.title,
            type.sum
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getBudgetTypes()
                }
            }
        })
    }

    fun editBudgetType(id: Int, type: BudgetType, loadList: Boolean = true) =
        viewModelScope.launch(Dispatchers.IO) {
            budgetTypeApiService.editBudgetType(
                Settings.token,
                id,
                type.title,
                type.sum
            ).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful && loadList) {
                        getBudgetTypes()
                    }
                }
            })
        }

    fun deleteBudgetType(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        budgetTypeApiService.deleteBudgetType(
            Settings.token,
            id
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getBudgetTypes()
                    getEarningTypes()
                    getExpenseTypes()
                    getOperations()
                }
            }
        })
    }

    fun makeExchange(from: BudgetType, to: BudgetType, sum: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            addOperation(
                Operation(
                    -1,
                    "exchange",
                    sum,
                    "",
                    to.id,
                    from.id,
                    "From ${from.title} to ${to.title}",
                    null,
                )
            )
        }

    fun getOperations() = viewModelScope.launch(Dispatchers.IO) {
        operationApiService.getOperations(
            Settings.token,
            page.value!!,
            operationListDate.value!!,
            operationType.value!!,
            if (operationListChosenBudgetType.value!! == 0) null else operationListChosenBudgetType.value!!,
            operationListStartWith.value!!,
            if (operationListChosenType.value!! == 0) null else operationListChosenType.value!!
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                val gson = Gson()
                if (response.isSuccessful) {
                    val listResponse =
                        gson.fromJson(
                            gson.toJson(response.body()),
                            OperationListResponse::class.java
                        )

                    isLastPage.value = listResponse.isLastPage
                    operations.value = listResponse.operations
                }
            }
        })
    }

    fun addManyOperation(operations: List<Operation>) =
        viewModelScope.launch(Dispatchers.IO) {
            operationApiService.addManyOperations(
                Settings.token,
                ManyOperationsBody(operations)
            ).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        getBudgetTypes()
                        getOperations()
                        getEarningSums(earningsDate.value!!)
                        getEarningTypes()
                        getExpenseSums(expensesDate.value!!)
                        getExpenseTypes()

                    }
                }
            })
        }

    fun addOperation(operation: Operation) =
        viewModelScope.launch(Dispatchers.IO) {
            operationApiService.addOperation(
                Settings.token,
                operation.title,
                operation.sum,
                operation.typeId,
                operation.budgetTypeId,
                operation.commentary,
                operation.isExpense
            ).enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    if (response.isSuccessful) {
                        getBudgetTypes()
                        getOperations()
                        if (operation.isExpense == false) {
                            getEarningSums(earningsDate.value!!)
                            getEarningTypes()
                        }
                        if (operation.isExpense == true) {
                            getExpenseSums(expensesDate.value!!)
                            getExpenseTypes()
                        }
                    }
                }
            })
        }

    fun editOperation(oldOperation: HistoryItem, newOperation: Operation): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            operationApiService.editOperation(
                Settings.token,
                oldOperation.id,
                newOperation.title,
                newOperation.sum,
                newOperation.typeId,
                newOperation.budgetTypeId,
                newOperation.commentary,
                newOperation.isExpense
            ).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        getBudgetTypes()
                        getOperations()
                        if (oldOperation.isExpense == false) {
                            getEarningSums(earningsDate.value!!)
                            getEarningTypes()
                        }
                        if (oldOperation.isExpense == true) {
                            getExpenseSums(expensesDate.value!!)
                            getExpenseTypes()
                        }
                    }
                }
            })
        }
        return true
    }

    fun deleteOperation(operation: HistoryItem): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            operationApiService.deleteOperation(
                Settings.token,
                operation.id
            ).enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    if (response.isSuccessful) {
                        getBudgetTypes()
                        getOperations()
                        if (operation.isExpense == true) {
                            getExpenseSums(expensesDate.value!!)
                            getExpenseTypes()
                        }
                        if (operation.isExpense == false) {
                            getEarningSums(earningsDate.value!!)
                            getEarningTypes()
                        }
                    }
                }
            })
        }
        return true
    }

    fun deleteAllOperations() = viewModelScope.launch(Dispatchers.IO) {
        operationApiService.deleteAllOperations(
            Settings.token
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getBudgetTypes()
                    getOperations()
                    getExpenseSums(expensesDate.value!!)
                    getEarningSums(earningsDate.value!!)
                    getExpenseTypes()
                    getEarningTypes()
                }
            }
        })
    }

    fun getEarningSums(period: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            operationApiService.getOperationSums(
                Settings.token,
                false,
                period
            ).enqueue(object : Callback<List<OperationSum>> {
                override fun onFailure(call: Call<List<OperationSum>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<OperationSum>>,
                    response: Response<List<OperationSum>>
                ) {
                    if (response.isSuccessful) {
                        earningSums.value = response.body()!!.toList()
                    }
                }
            })
        }

    fun getExpenseSums(period: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            operationApiService.getOperationSums(
                Settings.token,
                true,
                period
            ).enqueue(object : Callback<List<OperationSum>> {
                override fun onFailure(call: Call<List<OperationSum>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<OperationSum>>,
                    response: Response<List<OperationSum>>
                ) {
                    if (response.isSuccessful) {
                        expenseSums.value = response.body()!!.toList()
                    }
                }
            })
        }

    fun getEarningTypes() =
        viewModelScope.launch(Dispatchers.IO) {
            operationTypeApiService.getEarningTypes(
                Settings.token
            ).enqueue(object : Callback<List<OperationType>> {
                override fun onFailure(call: Call<List<OperationType>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<OperationType>>,
                    response: Response<List<OperationType>>
                ) {
                    if (response.isSuccessful) {
                        earningTypes.value = response.body()!!.toList()
                    }
                }
            })
        }

    fun getExpenseTypes() =
        viewModelScope.launch(Dispatchers.IO) {
            operationTypeApiService.getExpenseTypes(
                Settings.token
            ).enqueue(object : Callback<List<OperationType>> {
                override fun onFailure(call: Call<List<OperationType>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<OperationType>>,
                    response: Response<List<OperationType>>
                ) {
                    if (response.isSuccessful) {
                        expenseTypes.value = response.body()!!.toList()
                    }
                }
            })
        }

    fun addEarningType(title: String) = viewModelScope.launch(Dispatchers.IO) {
        operationTypeApiService.addEarningType(
            Settings.token,
            title
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getEarningTypes()
                }
            }
        })
    }

    fun addExpenseType(title: String) = viewModelScope.launch(Dispatchers.IO) {
        operationTypeApiService.addExpenseType(
            Settings.token,
            title
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getExpenseTypes()
                }
            }
        })
    }

    fun editEarningType(id: Int, title: String) = viewModelScope.launch(Dispatchers.IO) {
        operationTypeApiService.editEarningType(
            Settings.token,
            id,
            title
        ).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    getEarningTypes()
                }
            }
        })
    }

    fun editExpenseType(id: Int, title: String) = viewModelScope.launch(Dispatchers.IO) {
        operationTypeApiService.editExpenseType(
            Settings.token,
            id,
            title
        ).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    getExpenseTypes()
                }
            }
        })
    }

    fun deleteEarningType(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        operationTypeApiService.deleteEarningType(
            Settings.token,
            id
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getOperations()
                    getEarningTypes()
                    getBudgetTypes()
                }
            }
        })
    }

    fun deleteExpenseType(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        operationTypeApiService.deleteExpenseType(
            Settings.token,
            id
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getOperations()
                    getExpenseTypes()
                    getBudgetTypes()
                }
            }
        })
    }

    fun clearBudgetTypes() = viewModelScope.launch(Dispatchers.IO) {
        budgetTypeApiService.clearBudgetTypes(
            Settings.token
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getBudgetTypes()
                }
            }
        })
    }

    fun getChartOperations() = viewModelScope.launch(Dispatchers.IO) {
        operationApiService.getChartOperation(
            Settings.token,
            chartOperationPeriod.value!!,
            if (operationChartChosenBudgetType.value!! == 0) null else operationChartChosenBudgetType.value!!,
        ).enqueue(object : Callback<List<ChartOperation>> {
            override fun onFailure(call: Call<List<ChartOperation>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<List<ChartOperation>>,
                response: Response<List<ChartOperation>>
            ) {
                if (response.isSuccessful) {
                    chartOperations.value = response.body()!!.toList()
                }
            }
        })
    }

    fun getScheduledOperations(isExpense: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            scheduledOperationApiService.getOperations(
                Settings.token,
                isExpense
            ).enqueue(object : Callback<List<ScheduledOperation>> {
                override fun onFailure(call: Call<List<ScheduledOperation>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<ScheduledOperation>>,
                    response: Response<List<ScheduledOperation>>
                ) {
                    if (response.isSuccessful) {
                        scheduledOperationList.value = response.body()!!.toList()
                    }
                }
            })
        }

    fun addScheduledOperation(operation: ScheduledOperation) =
        viewModelScope.launch(Dispatchers.IO) {
            scheduledOperationApiService.addScheduledOperation(
                Settings.token,
                operation.title,
                operation.sum,
                operation.typeId,
                operation.budgetTypeId,
                operation.commentary,
                operation.isExpense
            ).enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    if (response.isSuccessful) {
                        getBudgetTypes()
                        getOperations()
                        if (!operation.isExpense) {
                            getEarningSums(earningsDate.value!!)
                            getEarningTypes()
                        }
                        if (operation.isExpense) {
                            getExpenseSums(expensesDate.value!!)
                            getExpenseTypes()
                        }
                    }
                }
            })
        }

    fun editScheduledOperation(id: Int, newOperation: ScheduledOperation) =
        viewModelScope.launch(Dispatchers.IO) {
            scheduledOperationApiService.editOperation(
                Settings.token,
                id,
                newOperation.title,
                newOperation.sum,
                newOperation.typeId,
                newOperation.budgetTypeId,
                newOperation.commentary,
                newOperation.isExpense
            ).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        getScheduledOperations(newOperation.isExpense)
                    }
                }
            })
        }

    fun deleteScheduledOperation(operationId: Int, isExpense: Boolean): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            scheduledOperationApiService.deleteOperation(
                Settings.token,
                operationId
            ).enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    if (response.isSuccessful) {
                        getScheduledOperations(isExpense)
                    }
                }
            })
        }
        return true
    }

    fun getPlannedBudget(month: Int, year: Int) = viewModelScope.launch(Dispatchers.IO) {
        plannedBudgetApiService.getPlannedBudget(
            Settings.token,
            month,
            year
        ).enqueue(object : Callback<PlannedBudget> {
            override fun onFailure(call: Call<PlannedBudget>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<PlannedBudget>,
                response: Response<PlannedBudget>
            ) {
                if (response.isSuccessful) {
                    plannedBudget.value = response.body()!!
                }
            }
        })
    }

    fun addPlannedBudget(budget: PlannedBudgetModel) = viewModelScope.launch(Dispatchers.IO) {
        plannedBudgetApiService.addPlannedBudget(
            Settings.token,
            budget.month,
            budget.year,
            budget.earnings,
            budget.expenses
        ).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful) {
                    getPlannedBudget(budget.month, budget.year)
                }
            }
        })
    }

    fun editPlannedBudget(id: Int, budget: PlannedBudgetModel) =
        viewModelScope.launch(Dispatchers.IO) {
            plannedBudgetApiService.editPlannedBudget(
                Settings.token,
                id,
                budget.month,
                budget.year,
                budget.earnings,
                budget.expenses
            ).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        getPlannedBudget(budget.month, budget.year)
                    }
                }
            })
        }

    fun deletePlannedBudget(id: Int, month: Int, year: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            plannedBudgetApiService.deletePlannedBudget(
                Settings.token,
                id
            ).enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    if (response.isSuccessful) {
                        getPlannedBudget(month, year)
                    }
                }
            })
        }
}