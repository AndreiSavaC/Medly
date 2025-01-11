package com.example.androidapp.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentService {
    @GET("appointment/{doctorId}/{date}")
    fun getAvailableHours(
        @Path("doctorId") doctorId: Int,
        @Path("date") date: String
    ): Call<List<String>>

    @POST("appointment")
    fun createAppointment(@Body body: Map<String, Any>): Call<ResponseBody>
}
