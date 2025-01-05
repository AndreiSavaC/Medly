package com.proiectpdm.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


@Serializable
data class User(
    val id: Int? = null,
    val keycloakId: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val birthday: String,
    val doctorId: Int? = null,
    val isDoctor: Boolean,
    val isAdmin: Boolean,
)

object UsersTable : Table(name = "users") {
    val id = integer("id").autoIncrement()
    val keycloakId = varchar("keycloak_id", 255)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 320).uniqueIndex()
    val gender = varchar("gender", 10)
    val height = float("height")
    val weight = float("weight")
    val birthday = varchar("birthday", 255)
    val doctorId = integer("doctor_id").references(UsersTable.id).nullable()
    val isDoctor = bool("is_doctor")
    val isAdmin = bool("is_admin")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
