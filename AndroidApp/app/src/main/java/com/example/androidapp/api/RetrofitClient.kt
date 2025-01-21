package com.example.androidapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://89.33.44.130:9090/"
    private const val FLASK_URL = "http://89.33.44.130:5000/"

    val appointmentService: AppointmentService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(AppointmentService::class.java)
    }

    val insuranceService: InsuranceService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(InsuranceService::class.java)
    }

    val userService: UserService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(UserService::class.java)
    }

    val symptomService: SymptomService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(SymptomService::class.java)
    }

    val reportService: ReportService by lazy {
        Retrofit.Builder().baseUrl(FLASK_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(ReportService::class.java)
    }

    val chatService: ChatService by lazy {
        Retrofit.Builder().baseUrl(FLASK_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(ChatService::class.java)
    }
}
