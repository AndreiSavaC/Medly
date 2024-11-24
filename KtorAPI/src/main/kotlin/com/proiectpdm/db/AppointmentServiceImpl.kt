package com.proiectpdm.db

import com.proiectpdm.model.Appointment
import com.proiectpdm.model.Appointments
import com.proiectpdm.model.Appointments.date
import com.proiectpdm.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class AppointmentServiceImpl : AppointmentService {
    private fun resultRowToAppointment(resultRow: ResultRow): Appointment {
        return Appointment(
            pacientId = resultRow[Appointments.pacientId],
            doctorId = resultRow[Appointments.doctorId],
            date = resultRow[Appointments.date],
            time = resultRow[Appointments.time],
            id = resultRow[Appointments.id],
        )
    }



    override suspend fun addAppointment(appointment: Appointment): Appointment? = dbQuery {
        val insertStmt = Appointments.insert {
            it[pacientId] = appointment.pacientId
            it[doctorId] = appointment.doctorId
            it[date] = appointment.date
            it[time] = appointment.time
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToAppointment(it) }
    }

    override suspend fun getAllAppointments(): List<Appointment> = dbQuery {
        Appointments.selectAll().map { resultRowToAppointment(it) }
    }

    override suspend fun getAppointmentByDate(date: String): List<Appointment> = dbQuery {
        Appointments.selectAll().where(Appointments.date.eq(date)).map { resultRowToAppointment(it) }
    }

    override suspend fun getAppointmentsByDoctorId(doctorId: Int): List<Appointment>  = dbQuery{
        Appointments.selectAll().where(Appointments.doctorId.eq(doctorId)).map { resultRowToAppointment(it) }
    }

    override suspend fun getAppointmentsByDoctorIdAndDate(doctorId: Int, date: String): List<Appointment> = dbQuery {
        Appointments.selectAll().where(Appointments.date.eq(date) and Appointments.doctorId.eq(doctorId)).map { resultRowToAppointment(it) }
    }

    override suspend fun deleteAppointment(id: Int): Boolean = dbQuery {
        Appointments.deleteWhere{Appointments.id eq id} > 0
    }

    override suspend fun getAppointmentById(id: Int): Appointment? = dbQuery {
        Appointments.selectAll().where(Appointments.id.eq(id)).map { resultRowToAppointment(it) }.singleOrNull()
    }

    override suspend fun updateAppointment(appointment: Appointment): Appointment?  = dbQuery{
        Appointments.update ({Appointments.id.eq(appointment.id)}){
            it[date] = appointment.date
            it[time] = appointment.time
            it[pacientId] = appointment.pacientId
            it[doctorId] = appointment.doctorId
        }
        getAppointmentById(appointment.id)
    }
}