package com.example.androidapp.api

import com.example.androidapp.models.Insurance
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InsuranceService {
    @GET("insurances/code/{code}")
    fun getInsuranceByCode(@Path("code") code: Double): Call<Insurance>
}