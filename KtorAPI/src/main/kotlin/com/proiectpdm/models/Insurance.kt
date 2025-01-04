package com.proiectpdm.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Insurance(
    val insuranceCode: Double,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthday: String,
    val doctorId: Int,
    val id: Int
)

object InsuranceTable : Table(name = "insurances") {

    val id = integer("id").autoIncrement()
    val insuranceCode = double("insurance_code").uniqueIndex()
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val gender = varchar("gender", 50)
    val birthday = varchar("birthday", 10)
    val doctorId = integer("doctor_id").references(UsersTable.id)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
