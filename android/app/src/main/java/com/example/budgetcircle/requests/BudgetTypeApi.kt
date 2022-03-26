package com.example.budgetcircle.requests

import com.example.budgetcircle.viewmodel.models.BudgetType
import retrofit2.Call
import retrofit2.http.*

interface BudgetTypeApi {
    @GET("/budgettype")
    fun getBudgetTypes(@Header("Authorization") token: String): Call<List<BudgetType>>

    @PUT("/budgettype")
    fun addBudgetType(
        @Header("Authorization") token: String,
        @Query("Title") title: String,
        @Query("Sum") sum: Double
    ): Call<Any>

    @POST("/budgettype/{id}")
    fun editBudgetType(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("Title") title: String,
        @Query("Sum") sum: Double
    ): Call<Void>

    @DELETE("/budgettype/{id}")
    fun deleteBudgetType(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Any>
}