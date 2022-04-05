package com.example.budgetcircle.requests

import com.example.budgetcircle.viewmodel.models.OperationType
import retrofit2.Call
import retrofit2.http.*

interface OperationTypeApi {
    @GET("/type/earning")
    fun getEarningTypes(
        @Header("Authorization") token: String
    ): Call<List<OperationType>>

    @GET("/type/expense")
    fun getExpenseTypes(
        @Header("Authorization") token: String
    ): Call<List<OperationType>>

    @PUT("/type/earning")
    fun addEarningType(
        @Header("Authorization") token: String,
        @Query("Title") title: String
    ): Call<Any>

    @PUT("/type/expense")
    fun addExpenseType(
        @Header("Authorization") token: String,
        @Query("Title") title: String
    ): Call<Any>

    @POST("/type/earning/{id}")
    fun editEarningType(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("Title") title: String
    ): Call<Void>

    @POST("/type/expense/{id}")
    fun editExpenseType(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("Title") title: String
    ): Call<Void>

    @DELETE("/type/earning/{id}")
    fun deleteEarningType(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Any>

    @DELETE("/type/expense/{id}")
    fun deleteExpenseType(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Any>
}