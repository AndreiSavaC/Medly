package com.proiectpdm.db

import com.proiectpdm.model.Pacients
import com.proiectpdm.model.Pacient
import com.proiectpdm.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PacientServiceImpl : PacientService {

    private fun resultRowToPacient(resultRow: ResultRow): Pacient {
        return Pacient(
            firstName = resultRow[Pacients.firstName],
            lastName = resultRow[Pacients.lastName],
            email = resultRow[Pacients.email],
            gender = resultRow[Pacients.gender],
            height = resultRow[Pacients.height],
            weight = resultRow[Pacients.weight],
            birthday = resultRow[Pacients.birthday],
            id = resultRow[Pacients.id]
        )
    }

    override suspend fun addPacient(pacient: Pacient): Pacient? = dbQuery {
        val insertStmt = Pacients.insert {
            it[firstName] = pacient.firstName
            it[lastName] = pacient.lastName
            it[email] = pacient.email
            it[gender] = pacient.gender
            it[height] = pacient.height
            it[weight] = pacient.weight
            it[birthday] = pacient.birthday
        }

        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToPacient(it) }
    }

    override suspend fun getAllPacients(): List<Pacient> = dbQuery {
        Pacients.selectAll().map { resultRowToPacient(it) }
    }

    override suspend fun deletePacient(id: Int): Boolean = dbQuery {
        Pacients.deleteWhere { Pacients.id eq id } > 0
    }

    override suspend fun getPacientById(id: Int): Pacient? = dbQuery {
        Pacients.selectAll().where { Pacients.id eq id }
            .map { resultRowToPacient(it) }
            .singleOrNull()
    }

    override suspend fun updatePacient(pacient: Pacient): Pacient? = dbQuery {
        Pacients.update({ Pacients.id eq pacient.id }) {
            it[firstName] = pacient.firstName
            it[lastName] = pacient.lastName
            it[email] = pacient.email
            it[gender] = pacient.gender
            it[height] = pacient.height
            it[weight] = pacient.weight
            it[birthday] = pacient.birthday
        }

        getPacientById(pacient.id)
    }
}
