package com.proiectpdm.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Appointment(
    val date:String,
    val time:String,
    val pacientId: Int,
    val doctorId: Int,
    val id:Int = 0
)

object Appointments:Table(){
    val id = integer("id").autoIncrement()
    val date = varchar("date",255)
    val time = varchar("time",255)
    val pacientId = integer("pacientId").references(Pacients.id)
    val doctorId = integer("doctorId").references(Doctors.id)

    override val primaryKey:PrimaryKey
    get() = PrimaryKey(id)
}