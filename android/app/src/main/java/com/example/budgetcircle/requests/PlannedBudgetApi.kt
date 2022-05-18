package com.example.budgetcircle.requests

import com.example.budgetcircle.viewmodel.models.PlannedBudget
import retrofit2.Call
import retrofit2.http.*

interface PlannedBudgetApi  {
    @PUT("/plannedbudget")
    fun addPlannedBudget(
        @Header("Authorization") token: String,
        @Query("Month") month: Int,
        @Query("Year") year: Int,
        @Query("Earnings") earnings: Double,
        @Query("Expenses") expenses: Double
    ): Call<Any>

    @POST("/plannedbudget/{id}")
    fun editPlannedBudget(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("Month") month: Int,
        @Query("Year") year: Int,
        @Query("Earnings") earnings: Double,
        @Query("Expenses") expenses: Double
    ): Call<Void>

    @GET("/plannedbudget")
    fun getPlannedBudget(
        @Header("Authorization") token: String,
        @Query("Month") month: Int,
        @Query("Year") year: Int
    ): Call<PlannedBudget>

    @DELETE("/plannedbudget/{id}")
    fun deletePlannedBudget(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Any>
}