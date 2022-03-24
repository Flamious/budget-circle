package com.example.budgetcircle.requests

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {
    @FormUrlEncoded
    @POST("/user/signin")
    fun signIn(@Field("Email") Email: String, @Field("Password") Password: String): Call<Any>

    @FormUrlEncoded
    @POST("/user/signup")
    fun signUp(
        @Field("Email") Email: String,
        @Field("Password") Password: String,
        @Field("ConfirmationPassword") ConfirmationPassword: String
    ): Call<Any>

    @POST("/user/token")
    fun refreshToken(@Header("Authorization") token: String): Call<Any>
}