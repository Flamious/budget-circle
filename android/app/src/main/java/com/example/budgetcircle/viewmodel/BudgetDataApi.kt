package com.example.budgetcircle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetcircle.MainActivity
import com.example.budgetcircle.R
import com.example.budgetcircle.requests.BudgetTypeApi
import com.example.budgetcircle.requests.Client
import com.example.budgetcircle.requests.OperationApi
import com.example.budgetcircle.requests.OperationTypeApi
import com.example.budgetcircle.viewmodel.models.BudgetType
import com.example.budgetcircle.viewmodel.models.Operation
import com.example.budgetcircle.viewmodel.models.OperationSum
import com.example.budgetcircle.viewmodel.models.OperationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BudgetDataApi(application: Application) : AndroidViewModel(application) {
    private val budgetTypeApiService: BudgetTypeApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(BudgetTypeApi::class.java)
    private val operationApiService: OperationApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(OperationApi::class.java)
    private val operationTypeApiService: OperationTypeApi =
        Client.getClient(getApplication<Application>().resources.getString(R.string.url))
            .create(OperationTypeApi::class.java)

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

    init {
        getBudgetTypes()
        getEarningTypes()
        getExpenseTypes()
    }

    private fun getBudgetTypes() = viewModelScope.launch(Dispatchers.IO) {
        budgetTypeApiService.getBudgetTypes(
            MainActivity.Token
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
            MainActivity.Token,
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
                MainActivity.Token,
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
            MainActivity.Token,
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
                }
            }
        })
    }

    fun addSum(budget: BudgetType, sum: Double, loadList: Boolean = true) {
        budget.sum += sum

        editBudgetType(
            budget.id,
            budget,
            loadList
        )
    }

    fun addSum(budgetId: Int, sum: Double, newList: Boolean = true) {
        val budget = budgetTypes.value!!.findLast { x -> x.id == budgetId }
        addSum(budget!!, sum, newList)
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
            addSum(to, sum, false)
            addSum(from, -sum, true)
        }

    fun addOperation(operation: Operation) =
        viewModelScope.launch(Dispatchers.IO) {
            if (operation.isExpense == true) addSum(operation.budgetTypeId, -operation.sum)
            if (operation.isExpense == false) addSum(operation.budgetTypeId, operation.sum)

            operationApiService.addOperation(
                MainActivity.Token,
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
                        if (operation.isExpense == false) {
                            getEarningSums(earningsDate.value!!)
                        }
                        if (operation.isExpense == true) {
                            getExpenseSums(expensesDate.value!!)
                        }
                    }
                }
            })
        }

    fun getEarningSums(period: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            operationApiService.getOperationSums(
                MainActivity.Token,
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
                MainActivity.Token,
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
                MainActivity.Token
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
                MainActivity.Token
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
}