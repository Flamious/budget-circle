package com.example.budgetcircle.requests

import com.example.budgetcircle.viewmodel.models.ChartOperation
import com.example.budgetcircle.viewmodel.models.OperationSum
import com.example.budgetcircle.viewmodel.models.ScheduledOperation
import retrofit2.Call
import retrofit2.http.*

interface ScheduledOperationApi {
    @PUT("/scheduledoperation")
    fun addScheduledOperation(
        @Header("Authorization") token: String,
        @Query("Title") title: String,
        @Query("Sum") sum: Double,
        @Query("TypeId") typeId: Int,
        @Query("BudgetTypeId") budgetTypeId: Int,
        @Query("Commentary") commentary: String,
        @Query("IsExpense") isExpense: Boolean
    ): Call<Any>

    @POST("/scheduledoperation/{id}")
    fun editOperation(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("Title") title: String,
        @Query("Sum") sum: Double,
        @Query("TypeId") typeId: Int,
        @Query("BudgetTypeId") budgetTypeId: Int,
        @Query("Commentary") commentary: String,
        @Query("IsExpense") isExpense: Boolean
    ): Call<Void>

    @GET("/scheduledoperation")
    fun getOperations(
        @Header("Authorization") token: String,
        @Query("IsExpense") isExpense: Boolean
    ): Call<List<ScheduledOperation>>

    @DELETE("/scheduledoperation/{id}")
    fun deleteOperation(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Any>
}