package com.example.budgetcircle.requests

import com.example.budgetcircle.viewmodel.models.OperationType
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface OperationTypeApi {
    @GET("/type/earning")
    fun getEarningTypes(
        @Header("Authorization") token: String
    ): Call<List<OperationType>>

    @GET("/type/expense")
    fun getExpenseTypes(
        @Header("Authorization") token: String
    ): Call<List<OperationType>>
}