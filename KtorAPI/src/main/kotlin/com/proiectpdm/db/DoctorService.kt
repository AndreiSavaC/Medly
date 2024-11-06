package com.proiectpdm.db

import com.proiectpdm.model.Doctor

interface DoctorService {
    suspend fun addDoctor(doctor: Doctor): Doctor?
    suspend fun getAllDoctors(): List<Doctor>
    suspend fun deleteDoctor(id: Int): Boolean
    suspend fun getDoctorById(id: Int): Doctor?
    suspend fun updateDoctor(doctor: Doctor): Doctor?
}