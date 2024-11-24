package com.example.androidapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AppointmentService {
    // Endpoint pentru a obține orele disponibile pentru un doctor și o anumită zi
    @GET("appointment/{doctorId}/{date}")
    fun getAvailableHours(
        @Path("doctorId") doctorId: Int,
        @Path("date") date: String
    ): Call<List<String>> // Aceasta va returna lista de ore disponibile
}
