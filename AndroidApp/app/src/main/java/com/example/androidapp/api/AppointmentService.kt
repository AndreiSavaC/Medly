package com.example.androidapp.api

import com.example.androidapp.models.Appointment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AppointmentService {
    @GET("appointments/free-slots/{doctorId}")
    fun getAvailableHours(
        @Path("doctorId") doctorId: Int,
        @Query("date") date: String
    ): Call<List<String>>

    @GET("appointments/patient/{patientId}")
    fun getAppointmentsByPatientId(
        @Path("patientId") patientId: Int
    ): Call<List<Appointment>>

    @POST("appointments")
    fun createAppointment(@Body body: Map<String, Any>): Call<ResponseBody>
}
