package com.proiectpdm.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Doctor(
    val firstName:String,
    val lastName:String,
    val email:String,
    val speciality:String,
    val id:Int=0
)

object Doctors:Table(){

    val id=integer("id").autoIncrement()
    val firstName=varchar("first_name",255)
    val lastName=varchar("last_name",255)
    val email = varchar("email", 320).uniqueIndex()
    val speciality=varchar("speciality",255)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}