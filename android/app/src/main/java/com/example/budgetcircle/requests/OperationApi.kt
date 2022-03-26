package com.example.budgetcircle.requests

import com.example.budgetcircle.viewmodel.models.OperationSum
import retrofit2.Call
import retrofit2.http.*

interface OperationApi {
    @PUT("/operation")
    fun addOperation(
        @Header("Authorization") token: String,
        @Query("Title") title: String,
        @Query("Sum") sum: Double,
        @Query("TypeId") typeId: Int,
        @Query("BudgetTypeId") budgetTypeId: Int,
        @Query("Commentary") commentary: String,
        @Query("IsExpense") isExpense: Boolean?
    ): Call<Any>

    @GET("/operation/sum")
    fun getOperationSums(
        @Header("Authorization") token: String,
        @Query("IsExpense") isExpense: Boolean,
        @Query("Period") Period: Int
    ): Call<List<OperationSum>>
}