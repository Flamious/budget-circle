package com.example.budgetcircle.requests.models

import com.google.gson.annotations.SerializedName

class AuthResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String
)

class ErrorResponse(
    @SerializedName("message") val message: String,
    @SerializedName("error") val error: String
)