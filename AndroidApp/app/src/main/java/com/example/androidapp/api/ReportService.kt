package com.example.androidapp.api

import com.example.androidapp.models.ReportRequest
import com.example.androidapp.models.ReportResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("/reports/doctor")
    fun generateDoctorReport(@Body request: ReportRequest): Call<ReportResponse>
}
