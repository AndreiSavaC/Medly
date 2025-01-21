package com.example.androidapp.models

data class UserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val birthday: String,
    val doctorId: Int?,
    val isDoctor: Boolean,
    val isAdmin: Boolean,
)

data class UserUpdateRequest(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val birthday: String,
    val doctorId: Int?,
    val isDoctor: Boolean,
    val isAdmin: Boolean,
)

data class UserResponse(
    val id: Int?,
    val email: String,
    val keycloakId: String?,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val birthday: String,
    val doctorId: Int?,
    val isDoctor: Boolean,
    val isAdmin: Boolean,
)
