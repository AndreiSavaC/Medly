package com.proiectpdm.db

import com.proiectpdm.model.Appointment

interface AppointmentService {
    suspend fun addAppointment(appointment: Appointment) : Appointment?
    suspend fun getAllAppointments() : List<Appointment>
    suspend fun getAppointmentByDate(date: String) : List<Appointment>
    suspend fun getAppointmentsByDoctorId(doctorId: Int) : List<Appointment>

    suspend fun getAppointmentByPacientId(pacientId: Int) : List<Appointment>

    suspend fun getAppointmentsByDoctorIdAndDate(doctorId: Int, date: String) : List<Appointment>
    suspend fun deleteAppointment(id:Int):Boolean
    suspend fun getAppointmentById(id:Int) : Appointment?
    suspend fun updateAppointment(appointment: Appointment) : Appointment?
}