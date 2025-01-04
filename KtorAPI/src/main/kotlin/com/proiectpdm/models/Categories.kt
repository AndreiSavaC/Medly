package com.proiectpdm.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class DiseaseCategory(
    val name: String, val id: Int = 0
)

object DiseaseCategoriesTable : Table(name = "disease_categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}