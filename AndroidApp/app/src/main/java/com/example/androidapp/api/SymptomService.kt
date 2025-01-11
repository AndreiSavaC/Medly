package com.example.androidapp.api

import retrofit2.Call
import retrofit2.http.GET

data class CategoryResponse(
    val id: Int,
    val name: String
)

data class SymptomResponse(
    val id: Int,
    val name: String,
    val categoryId: Int
)

interface SymptomService {
    @GET("categories")
    fun getCategories(): Call<List<CategoryResponse>>

    @GET("symptoms")
    fun getSymptoms(): Call<List<SymptomResponse>>
}
