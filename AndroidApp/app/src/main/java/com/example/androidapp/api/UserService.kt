package com.example.androidapp.api

import com.example.androidapp.models.UserRequest
import com.example.androidapp.models.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {
    @POST("/users")
    @Headers("Content-Type: application/json")  // You may need to adjust headers as per your API requirements
    fun createUser(
        @Body user: UserRequest,
        @Header("X-User-Password") password: String
    ): Call<UserResponse>  // Replace UserResponse with your response type
}