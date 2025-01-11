package com.proiectpdm.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class DiseaseSymptom(
    val id: Int? = null, val name: String, val categoryId: Int
)

object DiseaseSymptomsTable : Table(name = "disease_symptoms") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    val categoryId = integer("category").references(DiseaseCategoriesTable.id)

    override val primaryKey = PrimaryKey(id)
}