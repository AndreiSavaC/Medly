package com.proiectpdm.services.appointment

import com.proiectpdm.models.Appointment

interface AppointmentService {
    suspend fun getAppointments(): List<Appointment>
    suspend fun getAppointmentById(id: Int): Appointment?
    suspend fun getAppointmentsByDate(date: String): List<Appointment>
    suspend fun getAppointmentsByDoctorId(doctorId: Int): List<Appointment>
    suspend fun getAppointmentsByPatientId(patientId: Int): List<Appointment>
    suspend fun getAppointmentsByDoctorIdAndDate(doctorId: Int, date: String): List<Appointment>
    suspend fun addAppointment(appointment: Appointment): Appointment?
    suspend fun updateAppointment(appointment: Appointment): Boolean
    suspend fun deleteAppointment(id: Int): Boolean

}