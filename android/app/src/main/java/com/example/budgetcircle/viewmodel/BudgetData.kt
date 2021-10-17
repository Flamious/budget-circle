package com.example.budgetcircle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetcircle.R
import com.example.budgetcircle.viewmodel.items.BudgetType
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

    val budgetTypes: MutableLiveData<MutableList<BudgetType>> = MutableLiveData<MutableList<BudgetType>>().apply {
        value = mutableListOf()
        value?.add(BudgetType(0, 0f, "Cash", false))
        /*value?.add(BudgetType(1, 0f, "Bank", false))
        value?.add(BudgetType(2, 0f, "Stocks", false))
        value?.add(BudgetType(3, 0f, "Card", true))*/
    }

    fun addToOperationList(item: HistoryItem) {
        operations.value?.add(item)
    }

    fun addToBudgetTypesList(type: BudgetType) {
        budgetTypes.value?.add(type)
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