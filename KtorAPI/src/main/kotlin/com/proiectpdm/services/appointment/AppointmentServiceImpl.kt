package com.proiectpdm.services.appointment

import com.proiectpdm.models.Appointment
import com.proiectpdm.models.AppointmentsTable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentServiceImpl : AppointmentService {
    override suspend fun getAppointments(): List<Appointment> = transaction {
        AppointmentsTable.selectAll().map { resultRowToAppointment(it) }
    }

    override suspend fun getAppointmentById(id: Int): Appointment? = transaction {
        AppointmentsTable.selectAll().where { AppointmentsTable.id eq id }.singleOrNull()
            ?.let { resultRowToAppointment(it) }
    }

    override suspend fun getAppointmentsByDate(date: String): List<Appointment> = transaction {
        AppointmentsTable.selectAll().where { AppointmentsTable.date eq date }.map { resultRowToAppointment(it) }
    }

    override suspend fun getAppointmentsByDoctorId(doctorId: Int): List<Appointment> = transaction {
        AppointmentsTable.selectAll().where { AppointmentsTable.doctorId eq doctorId }
            .map { resultRowToAppointment(it) }
    }

    override suspend fun getAppointmentsByPatientId(patientId: Int): List<Appointment> = transaction {
        val currentDateTime = LocalDateTime.now()

        AppointmentsTable.selectAll().where {
            (AppointmentsTable.patientId eq patientId)
        }.map { resultRowToAppointment(it) }.filter {
            val appointmentDateTime = LocalDateTime.parse(
                "${it.date} ${it.time}", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            )
            appointmentDateTime.isAfter(currentDateTime)
        }
    }


    override suspend fun getAppointmentsByDoctorIdAndDate(doctorId: Int, date: String): List<Appointment> =
        transaction {
            AppointmentsTable.selectAll()
                .where { (AppointmentsTable.date eq date) and (AppointmentsTable.doctorId eq doctorId) }
                .map { resultRowToAppointment(it) }
        }

    override suspend fun addAppointment(appointment: Appointment): Appointment? = transaction {
        val insertStmt = AppointmentsTable.insert {
            it[patientId] = appointment.patientId
            it[doctorId] = appointment.doctorId
            it[date] = appointment.date
            it[time] = appointment.time
            it[symptoms] = Json.encodeToString(appointment.symptoms)
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToAppointment(it) }
    }

    override suspend fun updateAppointment(id: Int, appointment: Appointment): Boolean = transaction {
        AppointmentsTable.update({ AppointmentsTable.id eq id }) {
            it[date] = appointment.date
            it[time] = appointment.time
            it[patientId] = appointment.patientId
            it[doctorId] = appointment.doctorId
            it[symptoms] = Json.encodeToString(appointment.symptoms)
        } > 0
    }

    override suspend fun deleteAppointment(id: Int): Boolean = transaction {
        AppointmentsTable.deleteWhere { AppointmentsTable.id eq id } > 0
    }

    private fun resultRowToAppointment(resultRow: ResultRow): Appointment {
        return Appointment(
            patientId = resultRow[AppointmentsTable.patientId],
            doctorId = resultRow[AppointmentsTable.doctorId],
            date = resultRow[AppointmentsTable.date],
            time = resultRow[AppointmentsTable.time],
            symptoms = Json.decodeFromString(resultRow[AppointmentsTable.symptoms]),
            id = resultRow[AppointmentsTable.id],
        )
    }
}