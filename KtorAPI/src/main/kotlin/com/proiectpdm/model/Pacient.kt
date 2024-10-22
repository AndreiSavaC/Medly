package com.proiectpdm.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


@Serializable
data class Pacient(
    val firstName: String,
    val lastName: String,
    val email: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val birthday: String,
    val id: Int = 0
)

object Pacients : Table() {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 320).uniqueIndex()
    val gender = varchar("gender", 10)
    val height = float("height")
    val weight = float("weight")
    val birthday = varchar("birthday",255)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
