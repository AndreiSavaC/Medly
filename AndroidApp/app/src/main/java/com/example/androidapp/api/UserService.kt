package com.example.androidapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

data class UserResponse(
    val id: Int,
    val keycloakId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val gender: String,
    val height: Int,
    val weight: Int,
    val birthday: String,
    val doctorId: Int?,
    val isDoctor: Boolean,
    val isAdmin: Boolean,

    // alte field-uri, dacă există
)

// Interfața Retrofit pentru user
interface UserService {
    @GET("users/keycloak/{id}")
    fun getUserByKeycloakId(@Path("id") id: String): Call<UserResponse>
}
