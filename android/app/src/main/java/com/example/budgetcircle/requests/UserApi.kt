package com.example.budgetcircle.requests

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserApi {
    @FormUrlEncoded
    @POST("/user/signin")
    fun signIn(@Field("Email") Email: String, @Field("Password") Password: String): Call<Any>
}