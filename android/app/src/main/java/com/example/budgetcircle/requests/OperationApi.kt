package com.example.budgetcircle.requests

import com.example.budgetcircle.viewmodel.models.ChartOperation
import com.example.budgetcircle.viewmodel.models.Operation
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

    @POST("/operation/{id}")
    fun editOperation(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("Title") title: String,
        @Query("Sum") sum: Double,
        @Query("TypeId") typeId: Int,
        @Query("BudgetTypeId") budgetTypeId: Int,
        @Query("Commentary") commentary: String,
        @Query("IsExpense") isExpense: Boolean?
    ): Call<Void>

    @GET("/operation/sum")
    fun getOperationSums(
        @Header("Authorization") token: String,
        @Query("IsExpense") isExpense: Boolean,
        @Query("Period") Period: Int
    ): Call<List<OperationSum>>

    @GET("/operation/chart")
    fun getChartOperation(
        @Header("Authorization") token: String,
        @Query("Period") period: String,
        @Query("BudgetTypeId") budgetTypeId: Int?
    ): Call<List<ChartOperation>>

    @GET("/operation")
    fun getOperations(
        @Header("Authorization") token: String,
        @Query("Page") page: Int,
        @Query("Period") period: Int,
        @Query("Kind") kind: String,
        @Query("BudgetTypeId") budgetTypeId: Int?,
        @Query("Order") order: String,
        @Query("TypeId") typeId: Int?
    ): Call<Any>

    @DELETE("/operation/{id}")
    fun deleteOperation(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Any>

    @DELETE("/operation")
    fun deleteAllOperations(
        @Header("Authorization") token: String
    ): Call<Any>
}