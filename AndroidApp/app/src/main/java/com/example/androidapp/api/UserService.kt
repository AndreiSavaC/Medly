package com.example.androidapp.api

import com.example.androidapp.models.UserRequest
import com.example.androidapp.models.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @POST("/users")
    @Headers("Content-Type: application/json")
    fun createUser(
        @Body user: UserRequest,
        @Header("X-User-Password") password: String
    ): Call<UserResponse>

    @GET("users/keycloak/{id}")
    fun getUserByKeycloakId(@Path("id") id: String): Call<UserResponse>
}