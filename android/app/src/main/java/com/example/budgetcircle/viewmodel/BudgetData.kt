package com.example.budgetcircle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetcircle.viewmodel.items.HistoryItem

open class BudgetData : ViewModel() {
    val totalSum: MutableLiveData<Float> = MutableLiveData<Float>().apply {
        value = 0f
    }

    val expensesSum: MutableLiveData<Float> = MutableLiveData<Float>().apply {
        value = 0f
    }

    val earningsSum: MutableLiveData<Float> = MutableLiveData<Float>().apply {
        value = 0f
    }

    val operations: MutableLiveData<MutableList<HistoryItem>> = MutableLiveData<MutableList<HistoryItem>>().apply {
        value = mutableListOf()
    }

    fun addToOperationList(expense: HistoryItem) {
        operations.value?.add(expense)
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