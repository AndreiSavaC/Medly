package com.example.androidapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.55:8080/" // URL-ul de bază al API-ului tău

    val appointmentService: AppointmentService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convertește răspunsurile JSON în obiecte Kotlin
            .build()
            .create(AppointmentService::class.java)
    }
}
