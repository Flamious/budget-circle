package com.example.budgetcircle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BudgetData : ViewModel() {
    val totalSum: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}