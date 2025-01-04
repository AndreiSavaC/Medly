package com.proiectpdm.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Appointment(
    val date: String, val time: String, val patientId: Int, val doctorId: Int, val id: Int = 0
)

object AppointmentsTable : Table(name = "appointments") {
    val id = integer("id").autoIncrement()
    val date = varchar("date", 255)
    val time = varchar("time", 255)
    val patientId = integer("patient_id").references(UsersTable.id)
    val doctorId = integer("doctor_id").references(UsersTable.id)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}