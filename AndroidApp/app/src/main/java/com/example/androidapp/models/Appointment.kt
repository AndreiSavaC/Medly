package com.example.androidapp.models

data class Appointment(
    val date: String,
    val time: String,
    val pacientId: Int,
    val doctorId: Int,
    val id: Int
)