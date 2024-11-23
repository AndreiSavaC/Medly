package com.example.androidapp.api

import com.example.androidapp.models.Appointment
import retrofit2.Call
import retrofit2.http.GET

interface AppointmentService {
    @GET("appointment")
    fun getAppointments(): Call<List<Appointment>>
}