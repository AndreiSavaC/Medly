package com.example.androidapp.models

data class Appointment(
    val date: String,
    val time: String,
    val pacientId: String,
    val doctorId: Int,
    val id: Int
)