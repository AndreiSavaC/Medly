package com.example.androidapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.55:8080/")  // URL-ul API-ului
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val appointmentService: AppointmentService = retrofit.create(AppointmentService::class.java)
}