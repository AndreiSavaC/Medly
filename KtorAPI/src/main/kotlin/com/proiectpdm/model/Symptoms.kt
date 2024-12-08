package com.proiectpdm.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Symptom(
    val id:Int=0,
    val name:String,
    val categoryId: Int
)

object Symptoms : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val categoryId = integer("category").references(Categories.id)

    override val primaryKey = PrimaryKey(id)
}