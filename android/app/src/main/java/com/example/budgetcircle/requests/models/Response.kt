package com.example.budgetcircle.requests.models

import com.example.budgetcircle.viewmodel.models.Operation
import com.google.gson.annotations.SerializedName

class AuthResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String
)

class ErrorResponse(
    @SerializedName("message") val message: String,
    @SerializedName("error") val error: String
)

class OperationListResponse(
    @SerializedName("message") val message: String,
    @SerializedName("entitiesNumber") val entitiesNumber: Int,
    @SerializedName("operations") val operations: List<Operation>
)