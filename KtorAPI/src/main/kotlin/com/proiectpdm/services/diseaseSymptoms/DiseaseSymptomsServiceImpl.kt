package com.proiectpdm.services.diseaseSymptoms

import com.proiectpdm.models.*
import com.proiectpdm.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DiseaseSymptomsServiceImpl : DiseaseSymptomsService {


    override suspend fun getSymptoms(): List<DiseaseSymptom> = dbQuery {
        DiseaseSymptomsTable.selectAll().map { resultRowToSymptom(it) }
    }

    override suspend fun getSymptomById(id: Int): DiseaseSymptom? = dbQuery {
        DiseaseSymptomsTable.selectAll().where(DiseaseSymptomsTable.id eq id).singleOrNull()
            ?.let { resultRowToSymptom(it) }
    }

    override suspend fun getSymptomsByCategory(id: Int): List<DiseaseSymptom> = dbQuery {
        DiseaseSymptomsTable.selectAll().where(DiseaseSymptomsTable.categoryId eq id).map { resultRowToSymptom(it) }
    }

    override suspend fun addSymptom(symptom: DiseaseSymptom): DiseaseSymptom? = dbQuery {
        val insertStmt = DiseaseSymptomsTable.insert {
            it[name] = symptom.name
            it[categoryId] = symptom.categoryId
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToSymptom(it) }
    }

    override suspend fun updateSymptom(id: Int, symptoms: DiseaseSymptom): Boolean = dbQuery {
        if (symptoms.id == null) {
            throw IllegalArgumentException("Disease symptom ID cannot be null for update operation")
        }
        DiseaseSymptomsTable.update({ DiseaseSymptomsTable.id.eq(symptoms.id) }) {
            it[name] = symptoms.name
            it[categoryId] = symptoms.categoryId
        } > 0
    }

    override suspend fun deleteSymptom(id: Int): Boolean = dbQuery {
        DiseaseSymptomsTable.deleteWhere { DiseaseSymptomsTable.id eq id } > 0
    }

    private fun resultRowToSymptom(resultRow: ResultRow): DiseaseSymptom {
        return DiseaseSymptom(
            name = resultRow[DiseaseSymptomsTable.name],
            id = resultRow[DiseaseSymptomsTable.id],
            categoryId = resultRow[DiseaseSymptomsTable.categoryId]
        )
    }
}