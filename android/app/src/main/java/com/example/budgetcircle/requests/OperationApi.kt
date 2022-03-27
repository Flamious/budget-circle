package com.example.budgetcircle.requests

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

    @GET("/operation")
    fun getOperations(
        @Header("Authorization") token: String
    ): Call<Any>


    @DELETE("/operation/{id}")
    fun deleteOperation(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Any>
}