package com.example.androidapp.models

data class AppointmentResponse(
    val id: Int ,
    val date: String,
    val time: String,
    val patientId: Int,
    val doctorId: Int,
    val symptoms: List<String>
)
data class AppointmentRequest(
    val date: String,
    val time: String,
    val patientId: Int,
    val doctorId: Int,
    val symptoms: List<String>
)