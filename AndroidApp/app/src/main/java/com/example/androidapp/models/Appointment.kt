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
data class Appointment(
    val patientName: String,
    val date: String,
    val time: String,
    val symptoms: String,
    val email: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val birthday: String
)